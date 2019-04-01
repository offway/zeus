package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhShoppingCart;

/**
 * 购物车Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhShoppingCartService{

	PhShoppingCart save(PhShoppingCart phShoppingCart);
	
	PhShoppingCart findOne(Long id);
}
