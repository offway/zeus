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
import cn.offway.zeus.repository.PhShoppingCartRepository;
import cn.offway.zeus.service.PhGoodsPropertyService;
import cn.offway.zeus.service.PhGoodsStockService;
import cn.offway.zeus.service.PhShoppingCartService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;


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
			String key = phShoppingCart.getBrandId()+"#####"+phShoppingCart.getBrandName();
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
			s.put("brandId", keyArray[0]);
			s.put("brandName", keyArray[1]);
			s.put("goods", resultMap.get(key));
			list.add(s);
		}
		return jsonResultHelper.buildSuccessJsonResult(list);

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
