package cn.offway.zeus.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
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
import cn.offway.zeus.exception.StockException;
import cn.offway.zeus.repository.PhAddressRepository;
import cn.offway.zeus.repository.PhOrderGoodsRepository;
import cn.offway.zeus.repository.PhOrderInfoRepository;
import cn.offway.zeus.service.OrderTimeoutService;
import cn.offway.zeus.service.PhGoodsPropertyService;
import cn.offway.zeus.service.PhGoodsSpecialService;
import cn.offway.zeus.service.PhGoodsStockService;
import cn.offway.zeus.service.PhMerchantService;
import cn.offway.zeus.service.PhOrderInfoService;
import cn.offway.zeus.service.PhPreorderInfoService;
import cn.offway.zeus.service.PhShoppingCartService;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.service.PhVoucherInfoService;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import cn.offway.zeus.utils.MathUtils;
import io.netty.util.HashedWheelTimer;


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
	
	@Autowired
	private PhAddressRepository phAddressRepository;
	
	@Autowired
	private PhShoppingCartService phShoppingCartService;
	
	@Autowired
	private HashedWheelTimer hashedWheelTimer;
	
	@Autowired
	private PhGoodsSpecialService phGoodsSpecialService;
	
	
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
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {Exception.class,StockException.class})
	public JsonResult add(OrderAddDto orderAddDto) throws Exception,StockException{
		Long addrId = orderAddDto.getAddrId();
		Long userId = orderAddDto.getUserId();
		Long pVoucherId = orderAddDto.getVoucherId();
		double walletAmount = null==orderAddDto.getWalletAmount()?0D:orderAddDto.getWalletAmount();
		
		int addrcount = phAddressRepository.countByIdAndUserId(addrId,userId);
		if(addrcount==0){
			throw new Exception("地址错误");
		}
		
		if(walletAmount>0){
			PhUserInfo phUserInfo = phUserInfoService.findOne(userId);
			double balance = phUserInfo.getBalance()==null?0D:MathUtils.sub(phUserInfo.getBalance(), walletAmount);
			if(balance<0){
				throw new Exception("余额不足");
			}
			phUserInfo.setBalance(balance);
			phUserInfoService.save(phUserInfo);
		}
		
		double sumMVoucherAmount= 0D;
		double sumMailFee= 0D;
		double sumAllAmount= 0D;
		double sumALlPrice= 0D;
		double sumpVoucherAmout = 0D;
		double sumPriceByplatform= 0D;//除不能使用平台优惠券的商品总价
		
		
		double pVoucherAmount = 0D;
		if(null!=pVoucherId){
			PhVoucherInfo pphVoucherInfo =  phVoucherInfoService.findOne(pVoucherId);
			pVoucherAmount = pphVoucherInfo.getAmount();
			sumpVoucherAmout = pVoucherAmount;
		}

		Date now = new Date();
		
		//总订单号
		String preorderNo = generateOrderNo("10");
		
		List<PhOrderInfo> phOrderInfos = new ArrayList<>();
		List<PhOrderGoods> orderGoodss = new ArrayList<>();

		List<OrderMerchantDto> merchantDtos =  orderAddDto.getMerchantDtos();
		
		List<Long> stockIds = new ArrayList<>();
		for (OrderMerchantDto orderMerchantDto : merchantDtos) {
			
			String orderNo = generateOrderNo("11");

			Long merchantId = orderMerchantDto.getMerchantId();
			Long mVoucherId = orderMerchantDto.getVoucherId();
			List<OrderInitStockDto> stocks = orderMerchantDto.getStocks();
			double sumPrice = 0D;
			int sumCount = 0;
			for (OrderInitStockDto stock : stocks) {
				Long stockId = stock.getStockId();
				stockIds.add(stockId);
				PhGoodsStock phGoodsStock=  phGoodsStockService.findOne(stockId);
				double price = phGoodsStock.getPrice() * stock.getNum().intValue();
				sumPrice = MathUtils.add(sumPrice, price);
				sumCount += stock.getNum().intValue();
				
				int c = phGoodsStockService.updateStock(stockId, stock.getNum());
				if(c==0){
					throw new StockException("减库存失败stockId:{}"+stockId+"num:"+stock.getNum());
				}
				
				PhOrderGoods phOrderGoods = new PhOrderGoods();
				phOrderGoods.setPreorderNo(preorderNo);
				phOrderGoods.setBrandId(phGoodsStock.getBrandId());
				phOrderGoods.setBrandLogo(phGoodsStock.getBrandLogo());
				phOrderGoods.setBrandName(phGoodsStock.getBrandName());
				phOrderGoods.setCreateTime(now);
				phOrderGoods.setGoodsCount(stock.getNum());
				phOrderGoods.setGoodsId(phGoodsStock.getGoodsId());
				phOrderGoods.setGoodsImage(phGoodsStock.getImage());
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
				
				int count = phGoodsSpecialService.countByGoodsId(phOrderGoods.getGoodsId());
				if(count == 0){
					sumPriceByplatform = MathUtils.add(sumPriceByplatform, phOrderGoods.getPrice());
				}
				
			}
			String message = orderMerchantDto.getMessage();
			PhMerchant phMerchant = phMerchantService.findOne(merchantId);
			
			PhVoucherInfo mphVoucherInfo = null;
			if(null!=mVoucherId){
				mphVoucherInfo =  phVoucherInfoService.findOne(mVoucherId);
				sumMVoucherAmount = MathUtils.add(sumMVoucherAmount, mphVoucherInfo.getAmount());
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
			phOrderInfo.setIsHidden("0");
			
			//需要支付金额=商品总价+运费-商户优惠券金额
			double laveAmount = MathUtils.sub(MathUtils.add(sumPrice, mailFee), sumMVoucherAmount);
			double pVAmount =0D;
			if(pVoucherAmount>0){
				if(pVoucherAmount<=laveAmount){
					pVAmount = pVoucherAmount;
					pVoucherAmount = 0D;
				}else{
					pVAmount = laveAmount;
					pVoucherAmount = MathUtils.sub(pVoucherAmount, laveAmount);
				}
			}
			
			phOrderInfo.setPVoucherAmount(pVAmount);
			//需要支付金额=需要支付金额-平台优惠金额
			laveAmount = MathUtils.sub(laveAmount, pVAmount);
			phOrderInfo.setPVoucherId(pVoucherId);
			double pWAmount =0D;
			if(walletAmount>0){
				if(walletAmount<=laveAmount){
					pWAmount = walletAmount;
					walletAmount = 0D;
				}else{
					pWAmount = laveAmount;
					walletAmount=MathUtils.sub(walletAmount, laveAmount);
				}
			}
			//需要支付金额=需要支付金额-钱包金额
			laveAmount = MathUtils.sub(laveAmount, pWAmount);
			phOrderInfo.setWalletAmount(pWAmount);
			
			phOrderInfo.setAmount(laveAmount);
			phOrderInfo.setStatus("0");
			phOrderInfo.setUserId(userId);
			phOrderInfo.setVersion(0L);
			phOrderInfo.setAddrId(addrId);
			phOrderInfos.add(phOrderInfo);
			
			sumAllAmount = MathUtils.add(sumAllAmount, phOrderInfo.getAmount());
			sumALlPrice = MathUtils.add(sumALlPrice, phOrderInfo.getPrice());
			sumMailFee = MathUtils.add(sumMailFee, phOrderInfo.getMailFee());
			
			if(null !=mVoucherId){
				int c = phVoucherInfoService.updateStatusBym(mVoucherId, phOrderInfo.getPrice(), phOrderInfo.getMerchantId(),userId);
				if(c!=1){
					throw new Exception("锁定加息券异常");
				}
			}
			
		}
		phOrderInfoRepository.save(phOrderInfos);
		phOrderGoodsRepository.save(orderGoodss);
		
		if(null !=pVoucherId){
			int c = phVoucherInfoService.updateStatus(pVoucherId,sumPriceByplatform,userId);
			if(c!=1){
				throw new Exception("锁定加息券异常");
			}
		}
		
		if(walletAmount>0D){
			throw new Exception("钱包余额没用完,异常啦！！");
		}
		
		//清掉购物车
		phShoppingCartService.deleteByStockIds(stockIds);
		
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("preorderNo", preorderNo);
		PhPreorderInfo phPreorderInfo = new PhPreorderInfo();
		phPreorderInfo.setAmount(sumAllAmount);
		phPreorderInfo.setCreateTime(now);
		phPreorderInfo.setAddrId(addrId);
		phPreorderInfo.setMailFee(sumMailFee);
		phPreorderInfo.setOrderNo(preorderNo);
		phPreorderInfo.setPrice(sumALlPrice);
		phPreorderInfo.setPVoucherId(pVoucherId);
		if(sumAllAmount>0D){
			phPreorderInfo.setStatus("0");
			resultMap.put("paid", false);
		}else if(sumAllAmount==0D){
			//支付成功
			phPreorderInfo.setStatus("1");
			//更新订单状态
			phOrderInfoRepository.updateStatusByPreOrderNo(preorderNo,"0","1",null);
			resultMap.put("paid", true);
		}else{
			throw new Exception("金额计算错误");
		}
		phPreorderInfo.setUserId(userId);
		phPreorderInfo.setVersion(0L);
		phPreorderInfo.setVoucherAmount(MathUtils.add(sumMVoucherAmount, sumpVoucherAmout));
		phPreorderInfo.setWalletAmount(orderAddDto.getWalletAmount());
		phPreorderInfoService.save(phPreorderInfo);
		
		hashedWheelTimer.newTimeout(new OrderTimeoutService(preorderNo,phPreorderInfoService), 30, TimeUnit.MINUTES);
		return jsonResultHelper.buildSuccessJsonResult(resultMap);
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
				
				params.add(criteriaBuilder.equal(root.get("isHidden"), "0"));

				
				
                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime")));
				return null;
			}
		}, page);
	}

	@Override
	public int countByUserIdAndStatus(Long userId, String status) {
		return phOrderInfoRepository.countByUserIdAndStatusAndIsHidden(userId, status, "0");
	}
	
}
