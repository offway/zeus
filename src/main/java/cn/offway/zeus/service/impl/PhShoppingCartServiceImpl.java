package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.offway.zeus.domain.*;
import cn.offway.zeus.repository.PhPromotionRuleRepository;
import cn.offway.zeus.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import cn.offway.zeus.dto.OrderInitDto;
import cn.offway.zeus.dto.OrderInitStockDto;
import cn.offway.zeus.repository.PhLimitedSaleOpRepository;
import cn.offway.zeus.repository.PhMerchantFareRepository;
import cn.offway.zeus.repository.PhShoppingCartRepository;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import cn.offway.zeus.utils.MathUtils;


/**
 * 购物车Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhShoppingCartServiceImpl implements PhShoppingCartService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhShoppingCartRepository phShoppingCartRepository;
	
	@Autowired
	private PhGoodsStockService phGoodsStockService;
	
	@Autowired
	private PhGoodsPropertyService phGoodsPropertyService;
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhMerchantService phMerchantService;
	
	@Autowired
	private PhVoucherInfoService phVoucherInfoService;
	
	@Autowired
	private PhUserInfoService phUserInfoService;
	
	@Autowired
	private PhMerchantFareRepository phMerchantFareRepository;
	
	@Autowired
	private PhGoodsSpecialService phGoodsSpecialService;
	
	@Autowired
	private PhVoucherProjectService phVoucherProjectService;
	
	@Autowired
	private PhLimitedSaleService phLimitedSaleService;
	
	@Autowired
	private PhLimitedSaleOpRepository phLimitedSaleOpRepository;

	@Autowired
	private PhPromotionInfoService phPromotionInfoService;

	@Autowired
	private PhPromotionGoodsService phPromotionGoodsService;

	@Autowired
	private PhPromotionRuleService phPromotionRuleService;

	@Autowired
	private PhPromotionRuleRepository phPromotionRuleRepository;

	@Override
	public PhShoppingCart save(PhShoppingCart phShoppingCart){
		return phShoppingCartRepository.save(phShoppingCart);
	}
	
	@Override
	public PhShoppingCart findOne(Long id){
		return phShoppingCartRepository.findOne(id);
	}
	
	@Override
	public void delete(List<Long> ids){
		phShoppingCartRepository.delete(ids);
	}
	
	@Override
	public int deleteByStockIds(List<Long> ids){
		return phShoppingCartRepository.deleteByStockIds(ids);
	}


	@Override
	public JsonResult shopingCarListV2(Long userId,List<Long> scIds){

		double sumAmount = 0D;
		Map<String, Object> endMap = new HashMap<>();
		Map<Long, List<PhShoppingCart>> resultMap = new LinkedHashMap<>();
		List<PhShoppingCart> phShoppingCarts = phShoppingCartRepository.findByUserIdOrderByCreateTimeDesc(userId);
		for (PhShoppingCart phShoppingCart : phShoppingCarts) {
			Long goodsId = phShoppingCart.getGoodsId();
			Long promotionId = phPromotionGoodsService.findPromotionIdByGoodsId(goodsId);
			List<PhShoppingCart> carts = resultMap.get(promotionId);
			if(CollectionUtils.isEmpty(carts)){
				carts = new ArrayList<>();
			}
			if(scIds.contains(phShoppingCart.getId())){
				phShoppingCart.setRemark("1");
			}
			carts.add(phShoppingCart);
			resultMap.put(promotionId, carts);
		}

		List<Map<String, Object>> list = new ArrayList<>();
		for (Long promotionId : resultMap.keySet()) {
			PhPromotionInfo phPromotionInfo = null;
			Map<String,Object> map = new HashMap<>();
			if(null != promotionId){
				map.put("promotionId",promotionId);
				map.put("qucoudan",false);
				phPromotionInfo = phPromotionInfoService.findOne(promotionId);
				//减价类型[0-折扣，1-满减，2-赠品]
				String mode = phPromotionInfo.getMode();
				List<PhPromotionRule> phPromotionRules = phPromotionRuleRepository.findByPromotionId(promotionId);
				if("0".equals(mode)){
					//如果是折扣
					if(phPromotionRules.size()==1 && phPromotionRules.get(0).getDiscountNum().longValue()==1L){
						List<PhShoppingCart> shoppingCarts = resultMap.get(promotionId);
						boolean isSelect = false;
						double pAmount = 0D;
						for (PhShoppingCart phShoppingCart: shoppingCarts){
							if("1".equals(phShoppingCart.getRemark())){
								isSelect = true;
								pAmount = MathUtils.add(pAmount,MathUtils.mul(phShoppingCart.getPrice(),phShoppingCart.getGoodsCount()));
							}
						}
						//统一折扣
						if(isSelect){
							pAmount = MathUtils.mul(pAmount,phPromotionRules.get(0).getDiscountRate());
							sumAmount = MathUtils.add(sumAmount,pAmount);
							map.put("text","已满足全场"+phPromotionRules.get(0).getDiscountRate()*10+"折");
						}else{
							map.put("text","全场"+phPromotionRules.get(0).getDiscountRate()*10+"折");

						}
					}else{
						int count = 0;//选中件数
						List<PhShoppingCart> shoppingCarts = resultMap.get(promotionId);
						boolean isSelect = false;
						double pAmount = 0D;
						for (PhShoppingCart phShoppingCart: shoppingCarts){
							if("1".equals(phShoppingCart.getRemark())){
								isSelect = true;
								count+=phShoppingCart.getGoodsCount().intValue();
								pAmount = MathUtils.add(pAmount,MathUtils.mul(phShoppingCart.getPrice(),phShoppingCart.getGoodsCount()));

							}
						}
						if(isSelect){
							PhPromotionRule phPromotionRule = phPromotionRuleRepository.findByPromotionIdAndDiscountNum(promotionId,count);
							if(null !=phPromotionRule){
								pAmount = MathUtils.mul(pAmount,phPromotionRule.getDiscountRate());
								map.put("text","已满"+phPromotionRule.getDiscountNum()+"件"+phPromotionRule.getDiscountRate()*10+"折");
								//查询是否需要展示去凑单
								PhPromotionRule phPromotionRule1 = phPromotionRuleRepository.qucoudan(promotionId,phPromotionRule.getDiscountNum().intValue());
								if(null != phPromotionRule1){
									map.put("qucoudan",true);
								}
							}else{
								PhPromotionRule phPromotionRule1 = phPromotionRules.get(0);
								map.put("text","购买"+phPromotionRule1.getDiscountNum()+"件立享"+phPromotionRule1.getDiscountRate()*10+"折");
							}
							sumAmount = MathUtils.add(sumAmount,pAmount);
						}else{
							PhPromotionRule phPromotionRule = phPromotionRules.get(0);
							map.put("text","购买"+phPromotionRule.getDiscountNum()+"件立享"+phPromotionRule.getDiscountRate()*10+"折");
						}
					}
				}else if("1".equals(mode)){
					double amount = 0d;//选中总金额
					List<PhShoppingCart> shoppingCarts = resultMap.get(promotionId);
					boolean isSelect = false;
					for (PhShoppingCart phShoppingCart: shoppingCarts){
						if("1".equals(phShoppingCart.getRemark())){
							isSelect = true;
							amount = MathUtils.add(amount,MathUtils.mul(phShoppingCart.getPrice(),phShoppingCart.getGoodsCount()));
						}
					}
					if(isSelect){
						PhPromotionRule phPromotionRule = phPromotionRuleRepository.findByPromotionIdAnAndReduceLimit(promotionId,amount);
						if(null !=phPromotionRule){
							amount = MathUtils.sub(amount,phPromotionRule.getReduceAmount());
							map.put("text","以满足购满"+phPromotionRule.getReduceLimit()+"元减"+phPromotionRule.getReduceAmount()+"元");
							//查询是否需要展示去凑单
							PhPromotionRule phPromotionRule1 = phPromotionRuleRepository.qucoudanReduce(promotionId,phPromotionRule.getReduceLimit());
							if(null != phPromotionRule1){
								map.put("qucoudan",true);
							}
						}else{
							PhPromotionRule phPromotionRule1 = phPromotionRules.get(0);
							map.put("text","购满"+phPromotionRule1.getReduceLimit()+"元立减"+phPromotionRule1.getReduceAmount()+"元");
						}
						sumAmount = MathUtils.add(sumAmount,amount);

					}else{
						PhPromotionRule phPromotionRule = phPromotionRules.get(0);
						map.put("text","购满"+phPromotionRule.getReduceLimit()+"元立减"+phPromotionRule.getReduceAmount()+"元");
					}

				}else{
					double amount = 0d;//选中总金额
					List<PhShoppingCart> shoppingCarts = resultMap.get(promotionId);
					boolean isSelect = false;
					for (PhShoppingCart phShoppingCart: shoppingCarts){
						if("1".equals(phShoppingCart.getRemark())){
							isSelect = true;
							amount = MathUtils.add(amount,MathUtils.mul(phShoppingCart.getPrice(),phShoppingCart.getGoodsCount()));
						}
					}
					if(isSelect){

						PhPromotionRule phPromotionRule = phPromotionRuleRepository.findByPromotionIdAndGiftLimit(promotionId,amount);
						if(null !=phPromotionRule){
							map.put("text"," 已满足够"+phPromotionRule.getGiftLimit()+"元送"+phPromotionRule.getGift()+"元");
							//查询是否需要展示去凑单
							PhPromotionRule phPromotionRule1 = phPromotionRuleRepository.qucoudanGift(promotionId,phPromotionRule.getGiftLimit());
							if(null != phPromotionRule1){
								map.put("qucoudan",true);
							}
						}else{
							PhPromotionRule phPromotionRule1 = phPromotionRules.get(0);
							map.put("text","满"+phPromotionRule1.getGiftLimit()+"元立送"+phPromotionRule1.getGift());
						}
						sumAmount = MathUtils.add(sumAmount,amount);

					}else{
						PhPromotionRule phPromotionRule = phPromotionRules.get(0);
						map.put("text","满"+phPromotionRule.getGiftLimit()+"元立送"+phPromotionRule.getGift());
					}
				}
			}else {
				List<PhShoppingCart> shoppingCarts = resultMap.get(promotionId);
				double pAmount = 0D;
				for (PhShoppingCart phShoppingCart: shoppingCarts){
					if("1".equals(phShoppingCart.getRemark())){
						pAmount = MathUtils.add(pAmount,MathUtils.mul(phShoppingCart.getPrice(),phShoppingCart.getGoodsCount()));
					}
				}
				sumAmount = MathUtils.add(sumAmount,pAmount);
			}
			Map<String, Object> s = new HashMap<>();
			s.put("title", map);
			s.put("content", resultMap.get(promotionId));
			list.add(s);
		}
		endMap.put("sumAmount",sumAmount);
		endMap.put("list",list);
		return jsonResultHelper.buildSuccessJsonResult(endMap);

	}

	@Override
	public JsonResult shopingCarList(Long userId){
		
		Map<String, List<PhShoppingCart>> resultMap = new LinkedHashMap<>();
		List<PhShoppingCart> phShoppingCarts = phShoppingCartRepository.findByUserIdOrderByCreateTimeDesc(userId);
		for (PhShoppingCart phShoppingCart : phShoppingCarts) {
			String key = phShoppingCart.getBrandId()+"#####"+phShoppingCart.getMerchantId()+"#####"+phShoppingCart.getMerchantName();
			List<PhShoppingCart> carts = resultMap.get(key);
			if(CollectionUtils.isEmpty(carts)){
				carts = new ArrayList<>();
			}
			carts.add(phShoppingCart);
			resultMap.put(key, carts);
		}
		
		List<Map<String, Object>> list = new ArrayList<>();
		for (String key : resultMap.keySet()) {
			String[] keyArray = key.split("#####");
			Map<String, Object> s = new HashMap<>();
			Long merchantId = Long.valueOf(keyArray[1]);
			s.put("brandId", keyArray[0]);
			s.put("merchantId", merchantId);
			s.put("merchantName", keyArray[2]);
			s.put("goods", resultMap.get(key));
			list.add(s);
		}
		return jsonResultHelper.buildSuccessJsonResult(list);

	}
	
	@Override
	public JsonResult orderInit(OrderInitDto orderInitDto){
		
		Date now = new Date();
		Map<String, Object> result = new HashMap<>();
		Map<Long, Long> param = new HashMap<>();
		for (OrderInitStockDto  stock : orderInitDto.getStocks()) {
			param.put(stock.getStockId(), stock.getNum());
		}
		Long addrId = orderInitDto.getAddrId();
		Long userId = orderInitDto.getUserId();
		
		List<PhGoodsStock>  phGoodsStocks = phGoodsStockService.findByIdIn(param.keySet());

		List<Long> goodsIdAll = new ArrayList<>();
		double sumAmount = 0D;
		Map<Long, Integer> brandGoodsNum = new HashMap<>();
		Map<String, List<PhShoppingCart>> resultMap = new LinkedHashMap<>();
		for (PhGoodsStock phGoodsStock : phGoodsStocks) {
			
			Long goodsId = phGoodsStock.getGoodsId();
			goodsIdAll.add(goodsId);
			//限量发售检查
			PhLimitedSale phLimitedSale = phLimitedSaleService.findByGoodsId(goodsId);
			if(null != phLimitedSale){
				
				if("0".equals(phLimitedSale.getStatus()) || phLimitedSale.getBeginTime().after(now)
						|| phLimitedSale.getEndTime().before(now)){
					return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
				}
				
				int c = phLimitedSaleOpRepository.countByLimitedSaleIdAndUserIdAndType(phLimitedSale.getId(), userId, "0");
				if(c == 0){
					return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
				}
			}

			Long id = phGoodsStock.getId();
			PhShoppingCart phShoppingCart = new PhShoppingCart();
			phShoppingCart.setMerchantId(phGoodsStock.getMerchantId());
			phShoppingCart.setMerchantLogo(phGoodsStock.getMerchantLogo());
			phShoppingCart.setMerchantName(phGoodsStock.getMerchantName());
			phShoppingCart.setBrandId(phGoodsStock.getBrandId());
			phShoppingCart.setBrandLogo(phGoodsStock.getBrandLogo());
			phShoppingCart.setBrandName(phGoodsStock.getBrandName());
			phShoppingCart.setGoodsCount(param.get(id));
			phShoppingCart.setGoodsId(goodsId);
			phShoppingCart.setGoodsImage(phGoodsStock.getImage());
			phShoppingCart.setGoodsName(phGoodsStock.getGoodsName());
			phShoppingCart.setGoodsStockId(id);
			phShoppingCart.setPrice(phGoodsStock.getPrice());
			
			List<PhGoodsProperty> phGoodsProperties = phGoodsPropertyService.findByGoodsStockIdOrderBySortAsc(id);
			StringBuilder sb = new StringBuilder();
			for (PhGoodsProperty phGoodsProperty : phGoodsProperties) {
				sb.append(phGoodsProperty.getName()+":"+phGoodsProperty.getValue());
				sb.append(" ");
			}
			phShoppingCart.setProperty(sb.toString());
			
			
			String key = phShoppingCart.getBrandId()+"#####"+phShoppingCart.getMerchantId()+"#####"+phShoppingCart.getMerchantName();
			List<PhShoppingCart> carts = resultMap.get(key);
			if(CollectionUtils.isEmpty(carts)){
				carts = new ArrayList<>();
			}
			carts.add(phShoppingCart);
			resultMap.put(key, carts);
			
			Integer num = brandGoodsNum.get(phGoodsStock.getMerchantId());
			brandGoodsNum.put(phGoodsStock.getMerchantId(), (num==null?0:num.intValue())+phShoppingCart.getGoodsCount().intValue());

			int count = phGoodsSpecialService.countByGoodsId(phShoppingCart.getGoodsId());
			if(count == 0){
				sumAmount = MathUtils.add(sumAmount, MathUtils.mul(phShoppingCart.getPrice(), phShoppingCart.getGoodsCount().intValue()));
			}

			//计算
		}
		
		
		List<Map<String, Object>> list = new ArrayList<>();
		for (String key : resultMap.keySet()) {
			String[] keyArray = key.split("#####");
			Map<String, Object> s = new HashMap<>();
			Long merchantId = Long.valueOf(keyArray[1]);
			List<PhShoppingCart> carts = resultMap.get(key);
			s.put("brandId", keyArray[0]);
			s.put("merchantId", merchantId);
			s.put("merchantName", keyArray[2]);
			s.put("goods", carts);
			double fare = 0D;
			if(null !=addrId){
				fare = phMerchantService.calculateFare(merchantId, brandGoodsNum.get(merchantId), addrId);
			}
			s.put("fare", fare);
			//是否顺丰速运
			PhMerchantFare phMerchantFare = phMerchantFareRepository.findByMerchantIdAndIsDefault(merchantId, "1");

			String isSf = "0";
			if(null != phMerchantFare){
				isSf = phMerchantFare.getIsSf();
			}
			s.put("isSf", isSf);


			List<Long> goodsIds = new ArrayList<>();
			//查询可用店铺券
			double merchantSumAmount = 0D;
			for (PhShoppingCart c : carts) {
				merchantSumAmount= MathUtils.add(merchantSumAmount, MathUtils.mul(c.getPrice(), c.getGoodsCount().intValue()));
				goodsIds.add(c.getGoodsId());
			}
			s.put("merchantVouchers", phVoucherInfoService.findUseByMerchant(userId, merchantId, merchantSumAmount));
			List<PhVoucherProject> phVoucherProjects = phVoucherProjectService.findUseByMerchant(merchantId, merchantSumAmount);
			s.put("merchantVPs", phVoucherProjects);

			if(CollectionUtils.isEmpty(phVoucherProjects)){
				//没有商户优惠券的情况，查询是否有促销活动
				List<PhPromotionInfo> phPromotionInfos = phPromotionInfoService.findByMerchantIdAndGoodsId(merchantId,goodsIds);
				//商户下活动总优化金额
				double promotionAmount = 0D;
				List<PhPromotionInfo> reachPromotions = new ArrayList<>();
				for (PhPromotionInfo phPromotionInfo: phPromotionInfos) {
					//查询每个活动是否满足条件
					String mode = phPromotionInfo.getMode();//减价类型[0-折扣，1-满减，2-赠品]
					Long promotionId = phPromotionInfo.getId();

					if("1".equals(mode)){
						//计算满足满减条件的商品总金额
						double goodsAmount = 0D;
						for (PhShoppingCart c : carts){
							int count = phPromotionGoodsService.countByPromotionIdAndGoodsId(promotionId,c.getGoodsId());
							if(count>0){
								goodsAmount = MathUtils.add(goodsAmount, MathUtils.mul(c.getPrice(), c.getGoodsCount().intValue()));
							}
						}
						PhPromotionRule phPromotionRule = phPromotionRuleService.findByPromotionIdAnAndReduceLimit(promotionId,goodsAmount);
						if (null != phPromotionRule){
							promotionAmount = MathUtils.add(promotionAmount,phPromotionRule.getReduceAmount());
							reachPromotions.add(phPromotionInfo);
						}
					}else if("0".equals(mode)){
						//计算满足折扣的优惠金额
						int goodsCount = 0;
						double goodsAmount = 0D;
						for (PhShoppingCart c : carts){
							int count = phPromotionGoodsService.countByPromotionIdAndGoodsId(promotionId,c.getGoodsId());
							if(count>0){
								goodsCount += c.getGoodsCount().intValue();
								goodsAmount = MathUtils.add(goodsAmount, MathUtils.mul(c.getPrice(), c.getGoodsCount().intValue()));
							}
						}
						PhPromotionRule phPromotionRule = phPromotionRuleService.findByPromotionIdAndDiscountNum(promotionId,goodsCount);
						if (null != phPromotionRule){
							promotionAmount = MathUtils.add(promotionAmount,MathUtils.mul(goodsAmount,(1-phPromotionRule.getDiscountRate())));
							reachPromotions.add(phPromotionInfo);
						}
					}else if("2".equals(mode)){
						reachPromotions.add(phPromotionInfo);
					}
				}
				s.put("promotionAmount", promotionAmount);
				s.put("reachPromotions", reachPromotions);
			}

			list.add(s);
		}
		
		result.put("merchants", list);
		result.put("platformVouchers", phVoucherInfoService.findUseByPlatform(userId, sumAmount));
		PhUserInfo phUserInfo = phUserInfoService.findOne(userId);
		result.put("balance", phUserInfo.getBalance());
		//平台活动优惠金额
		double platformPromotionAmount = 0D;
		List<PhPromotionInfo> phPromotionInfos = phPromotionInfoService.findByPlatformAndGoodsId(goodsIdAll);
		for (PhPromotionInfo phPromotionInfo : phPromotionInfos){
			//计算满足折扣的优惠金额
			int goodsCount = 0;
			double goodsAmount = 0D;
			Long promotionId = phPromotionInfo.getId();
			for (PhGoodsStock c : phGoodsStocks){
				int count = phPromotionGoodsService.countByPromotionIdAndGoodsId(promotionId,c.getGoodsId());
				if(count>0){
					goodsCount += param.get(c.getId()).intValue();
					goodsAmount = MathUtils.add(goodsAmount, MathUtils.mul(c.getPrice(), param.get(c.getId()).intValue()));
				}
			}
			PhPromotionRule phPromotionRule = phPromotionRuleService.findByPromotionIdAndDiscountNum(promotionId,goodsCount);
			if (null != phPromotionRule){
				platformPromotionAmount = MathUtils.add(platformPromotionAmount,MathUtils.mul(goodsAmount,MathUtils.sub(1d,phPromotionRule.getDiscountRate())));
			}

		}
		result.put("platformPromotionAmount", platformPromotionAmount);

		return jsonResultHelper.buildSuccessJsonResult(result);

	}
	
	@Override
	public JsonResult shopingCar(Long userId,Long stockId,Long goodsCount){
		
		int count = phShoppingCartRepository.updateShoppingCar(userId,stockId,goodsCount);
		if(count==0){
			
			int cc = phShoppingCartRepository.countByUserId(userId);
			if(cc>=120){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.SHOPPING_CAR_LIMIT);
			}
			PhShoppingCart phShoppingCart = new PhShoppingCart();
			phShoppingCart.setCreateTime(new Date());
			phShoppingCart.setGoodsCount(goodsCount);
			phShoppingCart.setUserId(userId);
			phShoppingCart.setGoodsStockId(stockId);
			PhGoodsStock phGoodsStock = phGoodsStockService.findOne(stockId);
			
			phShoppingCart.setMerchantId(phGoodsStock.getMerchantId());
			phShoppingCart.setMerchantLogo(phGoodsStock.getMerchantLogo());
			phShoppingCart.setMerchantName(phGoodsStock.getMerchantName());
			phShoppingCart.setBrandId(phGoodsStock.getBrandId());
			phShoppingCart.setBrandLogo(phGoodsStock.getBrandLogo());
			phShoppingCart.setBrandName(phGoodsStock.getBrandName());
			phShoppingCart.setGoodsId(phGoodsStock.getGoodsId());
			phShoppingCart.setGoodsImage(phGoodsStock.getImage());
			phShoppingCart.setGoodsName(phGoodsStock.getGoodsName());
			phShoppingCart.setPrice(phGoodsStock.getPrice());
			
			List<PhGoodsProperty> phGoodsProperties = phGoodsPropertyService.findByGoodsStockIdOrderBySortAsc(stockId);
			StringBuilder sb = new StringBuilder();
			for (PhGoodsProperty phGoodsProperty : phGoodsProperties) {
				sb.append(phGoodsProperty.getName()+":"+phGoodsProperty.getValue());
				sb.append(" ");
			}
			phShoppingCart.setProperty(sb.toString());
			
			save(phShoppingCart);
		}
		
		return jsonResultHelper.buildSuccessJsonResult(null);
		
		
	}
}
