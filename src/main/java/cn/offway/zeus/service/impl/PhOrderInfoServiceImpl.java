package cn.offway.zeus.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaBuilder.In;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhGoodsProperty;
import cn.offway.zeus.domain.PhGoodsStock;
import cn.offway.zeus.domain.PhMerchant;
import cn.offway.zeus.domain.PhOrderGoods;
import cn.offway.zeus.domain.PhOrderInfo;
import cn.offway.zeus.domain.PhPreorderInfo;
import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.domain.PhVoucherInfo;
import cn.offway.zeus.dto.OrderAddDto;
import cn.offway.zeus.dto.OrderInitStockDto;
import cn.offway.zeus.dto.OrderMerchantDto;
import cn.offway.zeus.repository.PhOrderGoodsRepository;
import cn.offway.zeus.repository.PhOrderInfoRepository;
import cn.offway.zeus.service.PhGoodsPropertyService;
import cn.offway.zeus.service.PhGoodsStockService;
import cn.offway.zeus.service.PhMerchantService;
import cn.offway.zeus.service.PhOrderInfoService;
import cn.offway.zeus.service.PhPreorderInfoService;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.service.PhVoucherInfoService;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import cn.offway.zeus.utils.MathUtils;


/**
 * 订单Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhOrderInfoServiceImpl implements PhOrderInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhOrderInfoRepository phOrderInfoRepository;
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhMerchantService phMerchantService;
	
	@Autowired
	private PhVoucherInfoService phVoucherInfoService;
	
	@Autowired
	private PhGoodsStockService phGoodsStockService;
	
	@Autowired
	private PhGoodsPropertyService phGoodsPropertyService;
	
	@Autowired
	private PhOrderGoodsRepository phOrderGoodsRepository;
	
	@Autowired
	private PhUserInfoService phUserInfoService;
	
	@Autowired
	private PhPreorderInfoService phPreorderInfoService;
	
	@Override
	public PhOrderInfo save(PhOrderInfo phOrderInfo){
		return phOrderInfoRepository.save(phOrderInfo);
	}
	
	@Override
	public PhOrderInfo findOne(Long id){
		return phOrderInfoRepository.findOne(id);
	}
	
	@Override
	public List<PhOrderInfo> findByPreorderNoAndStatus(String preorderno,String status){
		return phOrderInfoRepository.findByPreorderNoAndStatus(preorderno, status);
	}
	
	@Override
	public PhOrderInfo findByOrderNo(String orderNo){
		return phOrderInfoRepository.findByOrderNo(orderNo);
	}

	
	@Override
	public String generateOrderNo(String prefix){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String date = sdf.format(new Date());
		String s = prefix+date;
		int countSequence =  phOrderInfoRepository.countSequence(s);
		if(countSequence==0){
			phOrderInfoRepository.sequence(s);
		}
		return phOrderInfoRepository.nextval(s);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = Exception.class)
	public JsonResult add(OrderAddDto orderAddDto) throws Exception{
		Long addrId = orderAddDto.getAddrId();
		Long userId = orderAddDto.getUserId();
		Long pVoucherId = orderAddDto.getVoucherId();
		Double walletAmount = orderAddDto.getWalletAmount();
		
		if(walletAmount>0){
			PhUserInfo phUserInfo = phUserInfoService.findOne(userId);
			phUserInfo.setBalance(phUserInfo.getBalance()-walletAmount);
			phUserInfoService.save(phUserInfo);
		}
		
		double sumVoucherAmount= 0D;
		double sumMailFee= 0D;
		double sumAllAmount= 0D;
		double sumALlPrice= 0D;
		
		
		double pVoucherAmount = 0D;
		if(null!=pVoucherId){
			PhVoucherInfo pphVoucherInfo =  phVoucherInfoService.findOne(pVoucherId);
			pVoucherAmount = pphVoucherInfo.getAmount();
			sumVoucherAmount+=pVoucherAmount;
		}

		Date now = new Date();
		
		//总订单号
		String preorderNo = generateOrderNo("PH");
		
		List<PhOrderInfo> phOrderInfos = new ArrayList<>();
		List<PhOrderGoods> orderGoodss = new ArrayList<>();

		List<OrderMerchantDto> merchantDtos =  orderAddDto.getMerchantDtos();
		for (OrderMerchantDto orderMerchantDto : merchantDtos) {
			
			String orderNo = generateOrderNo("PH");

			Long merchantId = orderMerchantDto.getMerchantId();
			Long mVoucherId = orderMerchantDto.getVoucherId();
			List<OrderInitStockDto> stocks = orderMerchantDto.getStocks();
			double sumPrice = 0D;
			int sumCount = 0;
			for (OrderInitStockDto stock : stocks) {
				Long stockId = stock.getStockId();
				PhGoodsStock phGoodsStock=  phGoodsStockService.findOne(stockId);
				double price = phGoodsStock.getPrice()*stock.getNum();
				sumPrice = MathUtils.add(sumPrice, price);
				sumCount += stock.getNum().intValue();
				
				int c = phGoodsStockService.updateStock(stockId, stock.getNum());
				if(c==0){
					throw new Exception("减库存失败stockId:{}"+stockId+"num:"+stock.getNum());
				}
				
				PhOrderGoods phOrderGoods = new PhOrderGoods();
				phOrderGoods.setPreorderNo(preorderNo);
				phOrderGoods.setBrandId(phGoodsStock.getBrandId());
				phOrderGoods.setBrandLogo(phGoodsStock.getBrandLogo());
				phOrderGoods.setBrandName(phGoodsStock.getBrandName());
				phOrderGoods.setCreateTime(now);
				phOrderGoods.setGoodsCount(stock.getNum());
				phOrderGoods.setGoodsId(phGoodsStock.getGoodsId());
				phOrderGoods.setGoodsImage(phGoodsStock.getGoodsImage());
				phOrderGoods.setGoodsName(phGoodsStock.getGoodsName());
				phOrderGoods.setGoodsStockId(phGoodsStock.getId());
				phOrderGoods.setMerchantId(phGoodsStock.getMerchantId());
				phOrderGoods.setMerchantLogo(phGoodsStock.getMerchantLogo());
				phOrderGoods.setMerchantName(phGoodsStock.getMerchantName());
				phOrderGoods.setOrderNo(orderNo);
				phOrderGoods.setPrice(price);
				
				List<PhGoodsProperty> phGoodsProperties = phGoodsPropertyService.findByGoodsStockIdOrderBySortAsc(phGoodsStock.getId());
				StringBuilder sb = new StringBuilder();
				for (PhGoodsProperty phGoodsProperty : phGoodsProperties) {
					sb.append(phGoodsProperty.getName()+":"+phGoodsProperty.getValue());
					sb.append(" ");
				}
				phOrderGoods.setRemark(sb.toString());
				orderGoodss.add(phOrderGoods);
				
			}
			String message = orderMerchantDto.getMessage();
			PhMerchant phMerchant = phMerchantService.findOne(merchantId);
			
			PhVoucherInfo mphVoucherInfo = null;
			if(null!=mVoucherId){
				mphVoucherInfo =  phVoucherInfoService.findOne(mVoucherId);
				sumVoucherAmount+=mphVoucherInfo.getAmount();
			}
			
			PhOrderInfo phOrderInfo = new PhOrderInfo();
			phOrderInfo.setCreateTime(now);
			phOrderInfo.setPreorderNo(preorderNo);
			double mailFee =phMerchantService.calculateFare(merchantId, sumCount, addrId);
			
			phOrderInfo.setMailFee(mailFee);
			phOrderInfo.setMerchantId(merchantId);
			phOrderInfo.setMerchantLogo(phMerchant.getLogo());
			phOrderInfo.setMerchantName(phMerchant.getName());
			phOrderInfo.setMessage(message);
			phOrderInfo.setMVoucherAmount(null==mphVoucherInfo?0D:mphVoucherInfo.getAmount());
			phOrderInfo.setMVoucherId(mVoucherId);
			phOrderInfo.setOrderNo(orderNo);
			phOrderInfo.setPrice(sumPrice);
			double pVAmount =0D;
			if(pVoucherAmount>0){
				pVAmount = pVoucherAmount>sumPrice?sumPrice:pVoucherAmount;
			}
			phOrderInfo.setPVoucherAmount(pVAmount);
			pVoucherAmount -= sumPrice;
			phOrderInfo.setPVoucherId(pVoucherId);
			double pWAmount =0D;
			if(walletAmount>0){
				pWAmount = walletAmount>sumPrice-pVAmount?sumPrice-pVAmount:walletAmount;
			}
			phOrderInfo.setWalletAmount(pWAmount);
			walletAmount -= sumPrice-pVAmount;
			
			phOrderInfo.setAmount(
					MathUtils.add(
							MathUtils.sub(
									MathUtils.sub(
											MathUtils.sub(phOrderInfo.getPrice(),phOrderInfo.getMVoucherAmount()),
									phOrderInfo.getPVoucherAmount()),
							phOrderInfo.getWalletAmount()),
					phOrderInfo.getMailFee()) );
			phOrderInfo.setStatus("0");
			phOrderInfo.setUserId(userId);
			phOrderInfo.setVersion(0L);
			phOrderInfo.setAddrId(addrId);
			phOrderInfos.add(phOrderInfo);
			
			sumAllAmount += phOrderInfo.getAmount();
			sumALlPrice += phOrderInfo.getPrice();
			sumMailFee += phOrderInfo.getMailFee();
			
			int c = phVoucherInfoService.updateStatusBym(mVoucherId, phOrderInfo.getPrice(), phOrderInfo.getMerchantId());
			if(c!=1){
				throw new Exception("锁定加息券异常");
			}
			
		}
		phOrderInfoRepository.save(phOrderInfos);
		phOrderGoodsRepository.save(orderGoodss);
		

		int c = phVoucherInfoService.updateStatus(pVoucherId,sumALlPrice);
		if(c!=1){
			throw new Exception("锁定加息券异常");
		}
		
		PhPreorderInfo phPreorderInfo = new PhPreorderInfo();
		phPreorderInfo.setAmount(sumAllAmount);
		phPreorderInfo.setCreateTime(now);
		phPreorderInfo.setAddrId(addrId);
		phPreorderInfo.setMailFee(sumMailFee);
		phPreorderInfo.setOrderNo(preorderNo);
		phPreorderInfo.setPrice(sumALlPrice);
		phPreorderInfo.setPVoucherId(pVoucherId);
		phPreorderInfo.setStatus("0");
		phPreorderInfo.setUserId(userId);
		phPreorderInfo.setVersion(0L);
		phPreorderInfo.setVoucherAmount(sumVoucherAmount);
		phPreorderInfo.setWalletAmount(orderAddDto.getWalletAmount());
		phPreorderInfoService.save(phPreorderInfo);
		
		return jsonResultHelper.buildSuccessJsonResult(preorderNo);
	}
	
	@Override
	public Page<PhOrderInfo> findByPage(final Long userId,final String status,Pageable page){
		return phOrderInfoRepository.findAll(new Specification<PhOrderInfo>() {
			
			@Override
			public Predicate toPredicate(Root<PhOrderInfo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				
				if(StringUtils.isNotBlank(status)){
					if("-1".equals(status)){
						//全部:交易关闭和交易完成
						In<String> in = criteriaBuilder.in(root.get("status"));
						in.value("3");
						in.value("4");
						params.add(in);
					}else{
						params.add(criteriaBuilder.equal(root.get("status"), status));
					}
				}
				
				if(null != userId){
					params.add(criteriaBuilder.equal(root.get("userId"), userId));
				}
				
				
                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime")));
				return null;
			}
		}, page);
	}
	
}
