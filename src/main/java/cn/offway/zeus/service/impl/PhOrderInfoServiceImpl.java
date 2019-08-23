package cn.offway.zeus.service.impl;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import cn.offway.zeus.domain.*;
import cn.offway.zeus.repository.*;
import cn.offway.zeus.service.*;
import cn.offway.zeus.utils.CommonResultCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.dto.OrderAddDto;
import cn.offway.zeus.dto.OrderInitStockDto;
import cn.offway.zeus.dto.OrderMerchantDto;
import cn.offway.zeus.exception.StockException;
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

	@Autowired
	private PhPromotionInfoService phPromotionInfoService;

	@Autowired
	private PhPromotionGoodsService phPromotionGoodsService;

	@Autowired
	private PhPromotionRuleService phPromotionRuleService;

	@Autowired
	private PhPromotionRuleRepository phPromotionRuleRepository;

	@Autowired
	private PhGoodsService phGoodsService;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private PhLimitedSaleService phLimitedSaleService;

	@Autowired
	private PhLimitedSaleOpRepository phLimitedSaleOpRepository;
	
	
	@Override
	public PhOrderInfo save(PhOrderInfo phOrderInfo){
		return phOrderInfoRepository.save(phOrderInfo);
	}
	
	@Override
	public PhOrderInfo findById(Long id){
		Optional<PhOrderInfo> optional = phOrderInfoRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
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
		String orderNo = phOrderInfoRepository.nextval(s);
		return Long.toOctalString(Long.parseLong(orderNo));
	}

	/**
	 * 检查是否存在限定商品
	 * @param orderAddDto
	 * @return
	 */
	public boolean containsLimitGoods(OrderAddDto orderAddDto){

		Set<Long> stockIds = new HashSet<>();
		for (OrderMerchantDto orderMerchantDto : orderAddDto.getMerchantDtos()) {
			List<OrderInitStockDto> stocks = orderMerchantDto.getStocks();
			for (OrderInitStockDto stock : stocks) {
				stockIds.add(stock.getStockId());
			}
		}
		return phGoodsService.containsLimitGoods(stockIds);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {Exception.class,StockException.class})
	public JsonResult add(OrderAddDto orderAddDto) throws Exception,StockException{

		//检查是否存在限定商品
		if(containsLimitGoods(orderAddDto)){
			return addLimit(orderAddDto);
		}else{
			return addCommon(orderAddDto);
		}

	}
	
	public JsonResult addLimit(OrderAddDto orderAddDto) throws Exception,StockException{

		Long addrId = orderAddDto.getAddrId();
		Long userId = orderAddDto.getUserId();
		double walletAmount = null==orderAddDto.getWalletAmount()?0D:orderAddDto.getWalletAmount();
		
		int addrcount = phAddressRepository.countByIdAndUserId(addrId,userId);
		if(addrcount==0){
			throw new Exception("地址错误");
		}
		
		if(walletAmount>0){
			PhUserInfo phUserInfo = phUserInfoService.findById(userId);
			double balance = phUserInfo.getBalance()==null?0D:MathUtils.sub(phUserInfo.getBalance(), walletAmount);
			if(balance<0){
				throw new Exception("余额不足");
			}
			phUserInfo.setBalance(balance);
			phUserInfoService.save(phUserInfo);
		}
		
		double sumMailFee= 0D;
		double sumAllAmount= 0D;
		double sumALlPrice= 0D;
		double sumPriceByplatform= 0D;//除不能使用平台优惠券的商品总价
		
		Date now = new Date();
		
		//总订单号
		String preorderNo = generateOrderNo("10");
		
		List<PhOrderInfo> phOrderInfos = new ArrayList<>();
		List<PhOrderGoods> orderGoodss = new ArrayList<>();

		List<OrderMerchantDto> merchantDtos =  orderAddDto.getMerchantDtos();

		List<Long> goodsIdAll = new ArrayList<>();
		List<OrderInitStockDto> stockArray = new ArrayList<>();


		for (OrderMerchantDto orderMerchantDto : merchantDtos) {
			List<OrderInitStockDto> stocks = orderMerchantDto.getStocks();
			stockArray.addAll(stocks);
			for (OrderInitStockDto stock : stocks) {
				Long stockId = stock.getStockId();
				PhGoodsStock phGoodsStock=  phGoodsStockService.findById(stockId);
				goodsIdAll.add(phGoodsStock.getGoodsId());
				//限量发售检查
				PhLimitedSale phLimitedSale = phLimitedSaleService.findByGoodsId(phGoodsStock.getGoodsId());
				if(null != phLimitedSale){

					if("0".equals(phLimitedSale.getStatus()) || phLimitedSale.getBeginTime().after(now)
							|| phLimitedSale.getEndTime().before(now)){
						return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
					}

					int c = phLimitedSaleOpRepository.countByLimitedSaleIdAndUserIdAndType(phLimitedSale.getId(), userId, "0");
					if(c < phLimitedSale.getBoostCount().intValue()){
						return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
					}
					int buyCount = phOrderGoodsRepository.sumGoodsCountByLimitSale(phLimitedSale.getGoodsId(),userId,phLimitedSale.getBeginTime(),phLimitedSale.getEndTime());
					if(buyCount+stock.getNum().intValue() > phLimitedSale.getBoostCount()){
						return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
					}
				}

			}
		}
		//检查是否有未上架的商品
		int off = phGoodsService.countByIdsAndStatus(goodsIdAll,"0");
		if(off > 0){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.GOODS_OFF);
		}


		List<Long> stockIds = new ArrayList<>();
		for (OrderMerchantDto orderMerchantDto : merchantDtos) {
			
			String orderNo = generateOrderNo("11");

			Long merchantId = orderMerchantDto.getMerchantId();
			List<OrderInitStockDto> stocks = orderMerchantDto.getStocks();
			double sumPrice = 0D;
			int sumCount = 0;
			List<Long> goodsIds = new ArrayList<>();
			for (OrderInitStockDto stock : stocks) {
				Long stockId = stock.getStockId();
				stockIds.add(stockId);
				PhGoodsStock phGoodsStock=  phGoodsStockService.findById(stockId);
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
				goodsIds.add(phOrderGoods.getGoodsId());
				
			}
			String message = orderMerchantDto.getMessage();
			PhMerchant phMerchant = phMerchantService.findById(merchantId);


			PhOrderInfo phOrderInfo = new PhOrderInfo();
			phOrderInfo.setCreateTime(now);
			phOrderInfo.setPreorderNo(preorderNo);
			double mailFee =phMerchantService.calculateFare(merchantId, sumCount, addrId);
			
			phOrderInfo.setMailFee(mailFee);
			phOrderInfo.setMerchantId(merchantId);
			phOrderInfo.setMerchantLogo(phMerchant.getLogo());
			phOrderInfo.setMerchantName(phMerchant.getName());
			phOrderInfo.setMessage(message);
			phOrderInfo.setMVoucherAmount(0D);
			phOrderInfo.setMVoucherId(null);
			phOrderInfo.setOrderNo(orderNo);
			phOrderInfo.setPrice(sumPrice);
			phOrderInfo.setPromotionAmount(0D);//商户促销优惠金额
			phOrderInfo.setIsHidden("0");
			phOrderInfo.setGift(null);


			//需要支付金额=商品总价+运费-商户优惠券金额-商户优惠金额
			double laveAmount = MathUtils.add(sumPrice, mailFee);

			phOrderInfo.setPVoucherAmount(0D);
			phOrderInfo.setPVoucherId(null);
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

			phOrderInfo.setPlatformPromotionAmount(0D);
			phOrderInfo.setAmount(laveAmount);
			phOrderInfo.setStatus("0");
			phOrderInfo.setUserId(userId);
			phOrderInfo.setVersion(0L);
			phOrderInfo.setAddrId(addrId);
			phOrderInfos.add(phOrderInfo);
			
			sumAllAmount = MathUtils.add(sumAllAmount, phOrderInfo.getAmount());
			sumALlPrice = MathUtils.add(sumALlPrice, phOrderInfo.getPrice());
			sumMailFee = MathUtils.add(sumMailFee, phOrderInfo.getMailFee());

		}
		phOrderInfoRepository.saveAll(phOrderInfos);
		phOrderGoodsRepository.saveAll(orderGoodss);
		
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
		phPreorderInfo.setPVoucherId(null);
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
		phPreorderInfo.setVoucherAmount(0D);
		phPreorderInfo.setWalletAmount(orderAddDto.getWalletAmount());
		phPreorderInfo.setPromotionAmount(0D);
		phPreorderInfoService.save(phPreorderInfo);
		
		hashedWheelTimer.newTimeout(new OrderTimeoutService(preorderNo,phPreorderInfoService), 30, TimeUnit.MINUTES);
		return jsonResultHelper.buildSuccessJsonResult(resultMap);
	}

	public JsonResult addCommon(OrderAddDto orderAddDto) throws Exception,StockException{


		Long addrId = orderAddDto.getAddrId();
		Long userId = orderAddDto.getUserId();
		Long pVoucherId = orderAddDto.getVoucherId();
		double walletAmount = null==orderAddDto.getWalletAmount()?0D:orderAddDto.getWalletAmount();

		int addrcount = phAddressRepository.countByIdAndUserId(addrId,userId);
		if(addrcount==0){
			throw new Exception("地址错误");
		}

		if(walletAmount>0){
			PhUserInfo phUserInfo = phUserInfoService.findById(userId);
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
			PhVoucherInfo pphVoucherInfo =  phVoucherInfoService.findById(pVoucherId);
			pVoucherAmount = pphVoucherInfo.getAmount();
			sumpVoucherAmout = pVoucherAmount;
		}



		Date now = new Date();

		//总订单号
		String preorderNo = generateOrderNo("10");

		List<PhOrderInfo> phOrderInfos = new ArrayList<>();
		List<PhOrderGoods> orderGoodss = new ArrayList<>();

		List<OrderMerchantDto> merchantDtos =  orderAddDto.getMerchantDtos();


		//平台活动优惠金额
		double platformPromotionAmount = 0D;
		double sumPromotionAmount = 0D;//保持不变
		List<Long> goodsIdAll = new ArrayList<>();
		List<OrderInitStockDto> stockArray = new ArrayList<>();


		for (OrderMerchantDto orderMerchantDto : merchantDtos) {
			List<OrderInitStockDto> stocks = orderMerchantDto.getStocks();
			stockArray.addAll(stocks);
			for (OrderInitStockDto stock : stocks) {
				Long stockId = stock.getStockId();
				PhGoodsStock phGoodsStock=  phGoodsStockService.findById(stockId);
				goodsIdAll.add(phGoodsStock.getGoodsId());
				//TODO chillhigh活动
				if(4465L == phGoodsStock.getGoodsId().longValue()){
					if(StringUtils.isBlank(stringRedisTemplate.opsForValue().get("zeus.chillhigh.share."+userId)) ||
							now.before(DateUtils.parseDate("2019-08-14 21:00:00","yyyy-MM-dd HH:mm:ss"))){
						return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
					}
				}
				//限量发售检查
				PhLimitedSale phLimitedSale = phLimitedSaleService.findByGoodsId(phGoodsStock.getGoodsId());
				if(null != phLimitedSale){

					if("0".equals(phLimitedSale.getStatus()) || phLimitedSale.getBeginTime().after(now)
							|| phLimitedSale.getEndTime().before(now)){
						return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
					}

					int c = phLimitedSaleOpRepository.countByLimitedSaleIdAndUserIdAndType(phLimitedSale.getId(), userId, "0");
					if(c < phLimitedSale.getBoostCount().intValue()){
						return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
					}
					int buyCount = phOrderGoodsRepository.sumGoodsCountByLimitSale(phLimitedSale.getGoodsId(),userId,phLimitedSale.getBeginTime(),phLimitedSale.getEndTime());
					if(buyCount+stock.getNum().intValue() > phLimitedSale.getBoostCount()){
						return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
					}

				}

			}
		}
		//检查是否有未上架的商品
		int off = phGoodsService.countByIdsAndStatus(goodsIdAll,"0");
		if(off > 0){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.GOODS_OFF);
		}

		List<Long> discountStockIds = new ArrayList<>();
		double discountRate = 1D;
		List<PhPromotionInfo> phPromotionInfos1 = phPromotionInfoService.findByPlatformAndGoodsId(goodsIdAll);
		for (PhPromotionInfo phPromotionInfo : phPromotionInfos1){
			//计算满足折扣的优惠金额
			int goodsCount = 0;
			double goodsAmount = 0D;
			Long promotionId = phPromotionInfo.getId();
			for (OrderInitStockDto stock : stockArray){
				Long stockId = stock.getStockId();
				PhGoodsStock phGoodsStock=  phGoodsStockService.findById(stockId);
				int count = phPromotionGoodsService.countByPromotionIdAndGoodsId(promotionId,phGoodsStock.getGoodsId());
				if(count>0){
					goodsCount += stock.getNum().intValue();
					goodsAmount = MathUtils.add(goodsAmount, MathUtils.mul(phGoodsStock.getPrice(), stock.getNum().intValue()));
					discountStockIds.add(phGoodsStock.getId());
				}
			}
			PhPromotionRule phPromotionRule = phPromotionRuleService.findByPromotionIdAndDiscountNum(promotionId,goodsCount);
			if (null != phPromotionRule){
				platformPromotionAmount = MathUtils.add(platformPromotionAmount,MathUtils.mul(goodsAmount,MathUtils.sub(1d,phPromotionRule.getDiscountRate())));
				discountRate = phPromotionRule.getDiscountRate();
			}

		}
		sumPromotionAmount = platformPromotionAmount;


		//商户优惠总额-不用了
		//double sumMPromotionAmount = 0D;
		List<Long> stockIds = new ArrayList<>();
		for (OrderMerchantDto orderMerchantDto : merchantDtos) {

			String orderNo = generateOrderNo("11");

			Long merchantId = orderMerchantDto.getMerchantId();
			Long mVoucherId = orderMerchantDto.getVoucherId();
			List<OrderInitStockDto> stocks = orderMerchantDto.getStocks();
			double sumPrice = 0D;
			int sumCount = 0;
			double mVoucherAmount = 0D;
			List<Long> goodsIds = new ArrayList<>();
			for (OrderInitStockDto stock : stocks) {
				Long stockId = stock.getStockId();
				stockIds.add(stockId);
				PhGoodsStock phGoodsStock=  phGoodsStockService.findById(stockId);
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
				if(discountStockIds.contains(phOrderGoods.getGoodsStockId())){
					//计算折扣金额
					phOrderGoods.setAmount(MathUtils.mul(price,discountRate));
				}

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
				goodsIds.add(phOrderGoods.getGoodsId());

			}
			String message = orderMerchantDto.getMessage();
			PhMerchant phMerchant = phMerchantService.findById(merchantId);

			//商户下活动总优惠金额
			double promotionAmount = 0D;

			PhVoucherInfo mphVoucherInfo = null;

			StringBuilder gift = new StringBuilder();

			if(null!=mVoucherId){
				mphVoucherInfo =  phVoucherInfoService.findById(mVoucherId);
				sumMVoucherAmount = MathUtils.add(sumMVoucherAmount, mphVoucherInfo.getAmount());
				mVoucherAmount = mphVoucherInfo.getAmount();
			}else{

				//没有商户优惠券的情况，查询是否有促销活动
				List<PhPromotionInfo> phPromotionInfos = phPromotionInfoService.findByMerchantIdAndGoodsId(merchantId,goodsIds);
				for (PhPromotionInfo phPromotionInfo: phPromotionInfos) {
					//查询每个活动是否满足条件
					String mode = phPromotionInfo.getMode();//减价类型[0-折扣，1-满减，2-赠品]
					Long promotionId = phPromotionInfo.getId();

					if("1".equals(mode)){
						//计算满足满减条件的商品总金额
						double goodsAmount = 0D;
						for (OrderInitStockDto stock : stocks){
							PhGoodsStock phGoodsStock=  phGoodsStockService.findById(stock.getStockId());
							int count = phPromotionGoodsService.countByPromotionIdAndGoodsId(promotionId,phGoodsStock.getGoodsId());
							if(count>0){
								goodsAmount = MathUtils.add(goodsAmount, MathUtils.mul(phGoodsStock.getPrice(), stock.getNum().intValue()));
							}
						}
						PhPromotionRule phPromotionRule = phPromotionRuleService.findByPromotionIdAnAndReduceLimit(promotionId,goodsAmount);
						if (null != phPromotionRule){
							promotionAmount = MathUtils.add(promotionAmount,phPromotionRule.getReduceAmount());
						}
					}else if("0".equals(mode)){
						//计算满足折扣的优惠金额
						int goodsCount = 0;
						double goodsAmount = 0D;
						for (OrderInitStockDto stock : stocks){
							PhGoodsStock phGoodsStock=  phGoodsStockService.findById(stock.getStockId());
							int count = phPromotionGoodsService.countByPromotionIdAndGoodsId(promotionId,phGoodsStock.getGoodsId());
							if(count>0){
								goodsCount += stock.getNum().intValue();
								goodsAmount = MathUtils.add(goodsAmount, MathUtils.mul(phGoodsStock.getPrice(), stock.getNum().intValue()));
							}
						}
						PhPromotionRule phPromotionRule = phPromotionRuleService.findByPromotionIdAndDiscountNum(promotionId,goodsCount);
						if (null != phPromotionRule){
							promotionAmount = MathUtils.add(promotionAmount,MathUtils.mul(goodsAmount,MathUtils.sub(1D,phPromotionRule.getDiscountRate())));
						}
					}else if("2".equals(mode)){
						//计算满足满减条件的商品总金额
						double goodsAmount = 0D;
						for (OrderInitStockDto stock : stocks){
							PhGoodsStock phGoodsStock=  phGoodsStockService.findById(stock.getStockId());
							int count = phPromotionGoodsService.countByPromotionIdAndGoodsId(promotionId,phGoodsStock.getGoodsId());
							if(count>0){
								goodsAmount = MathUtils.add(goodsAmount, MathUtils.mul(phGoodsStock.getPrice(), stock.getNum().intValue()));
							}
						}
						PhPromotionRule phPromotionRule = phPromotionRuleService.findByPromotionIdAndGiftLimit(promotionId,goodsAmount);
						if (null != phPromotionRule){
							gift.append(phPromotionRule.getGift()).append(" ");
						}

					}
				}

				sumPromotionAmount = MathUtils.add(sumPromotionAmount,promotionAmount);
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
			phOrderInfo.setPromotionAmount(promotionAmount);//商户促销优惠金额
			phOrderInfo.setIsHidden("0");
			phOrderInfo.setGift(gift.toString());


			//需要支付金额=商品总价+运费-商户优惠券金额-商户优惠金额
			double laveAmount = MathUtils.sub(MathUtils.add(sumPrice, mailFee), mVoucherAmount);
			laveAmount = MathUtils.sub(laveAmount,promotionAmount);
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


			double ppAmount =0D;
			if(platformPromotionAmount>0){
				if(platformPromotionAmount<=laveAmount){
					ppAmount = platformPromotionAmount;
					platformPromotionAmount = 0D;
				}else{
					ppAmount = laveAmount;
					platformPromotionAmount=MathUtils.sub(platformPromotionAmount, laveAmount);
				}
			}
			//需要支付金额=需要支付金额-钱包金额
			laveAmount = MathUtils.sub(laveAmount, ppAmount);
			phOrderInfo.setPlatformPromotionAmount(ppAmount);

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
		phOrderInfoRepository.saveAll(phOrderInfos);
		phOrderGoodsRepository.saveAll(orderGoodss);

		if(null !=pVoucherId){
			int c = phVoucherInfoService.updateStatus(pVoucherId,MathUtils.sub(sumPriceByplatform,sumPromotionAmount),userId);
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
		phPreorderInfo.setPromotionAmount(sumPromotionAmount);
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
