package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhPreorderGoods;

/**
 * 预生成订单商品Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhPreorderGoodsService{

	PhPreorderGoods save(PhPreorderGoods phPreorderGoods);
	
	PhPreorderGoods findOne(Long id);
}
