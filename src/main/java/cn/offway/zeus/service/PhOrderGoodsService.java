package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhOrderGoods;

/**
 * 订单商品Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhOrderGoodsService{

	PhOrderGoods save(PhOrderGoods phOrderGoods);
	
	PhOrderGoods getOne(Long id);

	List<PhOrderGoods> findByOrderNo(String orderNo);
}
