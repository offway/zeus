package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import cn.offway.zeus.domain.PhGoodsProperty;
import cn.offway.zeus.domain.PhGoodsStock;
import cn.offway.zeus.domain.PhShoppingCart;
import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.dto.OrderInitDto;
import cn.offway.zeus.dto.OrderInitStockDto;
import cn.offway.zeus.repository.PhShoppingCartRepository;
import cn.offway.zeus.service.PhGoodsPropertyService;
import cn.offway.zeus.service.PhGoodsStockService;
import cn.offway.zeus.service.PhMerchantService;
import cn.offway.zeus.service.PhShoppingCartService;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.service.PhVoucherInfoService;
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
		
		Map<String, Object> result = new HashMap<>();
		Map<Long, Long> param = new HashMap<>();
		for (OrderInitStockDto  stock : orderInitDto.getStocks()) {
			param.put(stock.getStockId(), stock.getNum());
		}
		Long addrId = orderInitDto.getAddrId();
		Long userId = orderInitDto.getUserId();
		
		List<PhGoodsStock>  phGoodsStocks = phGoodsStockService.findByIdIn(param.keySet());
		
		double sumAmount = 0D;
		Map<Long, Integer> brandGoodsNum = new HashMap<>();
		Map<String, List<PhShoppingCart>> resultMap = new LinkedHashMap<>();
		for (PhGoodsStock phGoodsStock : phGoodsStocks) {
			
			Long id = phGoodsStock.getId();
			PhShoppingCart phShoppingCart = new PhShoppingCart();
			phShoppingCart.setMerchantId(phGoodsStock.getMerchantId());
			phShoppingCart.setMerchantLogo(phGoodsStock.getMerchantLogo());
			phShoppingCart.setMerchantName(phGoodsStock.getMerchantName());
			phShoppingCart.setBrandId(phGoodsStock.getBrandId());
			phShoppingCart.setBrandLogo(phGoodsStock.getBrandLogo());
			phShoppingCart.setBrandName(phGoodsStock.getBrandName());
			phShoppingCart.setGoodsCount(param.get(id));
			phShoppingCart.setGoodsId(phGoodsStock.getGoodsId());
			phShoppingCart.setGoodsImage(phGoodsStock.getGoodsImage());
			phShoppingCart.setGoodsName(phGoodsStock.getGoodsName());
			phShoppingCart.setGoodsStockId(id);
			phShoppingCart.setPrice(phGoodsStock.getPrice());
			
			List<PhGoodsProperty> phGoodsProperties = phGoodsPropertyService.findByGoodsStockIdOrderBySortAsc(id);
			StringBuilder sb = new StringBuilder();
			for (PhGoodsProperty phGoodsProperty : phGoodsProperties) {
				sb.append(phGoodsProperty.getName()+":"+phGoodsProperty.getValue());
				sb.append("#");
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

			sumAmount+=phShoppingCart.getPrice();
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
			
			//查询可用店铺券
			double merchantSumAmount = 0D;
			for (PhShoppingCart c : carts) {
				merchantSumAmount+=c.getPrice();
			}
			s.put("merchantVoucherCount", phVoucherInfoService.countUseByMerchant(userId, merchantId, merchantSumAmount));
			
			list.add(s);
		}
		
		result.put("merchants", list);
		result.put("platformVoucherCount", phVoucherInfoService.countUseByPlatform(userId, sumAmount));
		PhUserInfo phUserInfo = phUserInfoService.findOne(userId);
		result.put("balance", phUserInfo.getBalance());
		
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
			phShoppingCart.setGoodsImage(phGoodsStock.getGoodsImage());
			phShoppingCart.setGoodsName(phGoodsStock.getGoodsName());
			phShoppingCart.setPrice(phGoodsStock.getPrice());
			
			List<PhGoodsProperty> phGoodsProperties = phGoodsPropertyService.findByGoodsStockIdOrderBySortAsc(stockId);
			StringBuilder sb = new StringBuilder();
			for (PhGoodsProperty phGoodsProperty : phGoodsProperties) {
				sb.append(phGoodsProperty.getName()+":"+phGoodsProperty.getValue());
				sb.append("#");
			}
			phShoppingCart.setProperty(sb.toString());
			
			save(phShoppingCart);
		}
		
		return jsonResultHelper.buildSuccessJsonResult(null);
		
		
	}
}
