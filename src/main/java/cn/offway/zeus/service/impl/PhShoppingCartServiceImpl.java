package cn.offway.zeus.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import cn.offway.zeus.domain.PhGoodsProperty;
import cn.offway.zeus.domain.PhGoodsStock;
import cn.offway.zeus.domain.PhShoppingCart;
import cn.offway.zeus.repository.PhShoppingCartRepository;
import cn.offway.zeus.service.PhGoodsPropertyService;
import cn.offway.zeus.service.PhGoodsStockService;
import cn.offway.zeus.service.PhShoppingCartService;
import io.swagger.annotations.ApiParam;


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
	
	@Override
	public PhShoppingCart save(PhShoppingCart phShoppingCart){
		return phShoppingCartRepository.save(phShoppingCart);
	}
	
	@Override
	public PhShoppingCart findOne(Long id){
		return phShoppingCartRepository.findOne(id);
	}
	
	
	@Override
	public void shopingCar(Long userId,Long stockId,Long goodsCount){
		
		int count = phShoppingCartRepository.updateShoppingCar(userId,stockId,goodsCount);
		if(count==0){
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
		
		
	}
}
