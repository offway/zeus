package cn.offway.zeus.service;

import java.util.Date;
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
	
	PhOrderGoods findById(Long id);

    int sumGoodsCountByOrderNo(String orderNo);

    List<PhOrderGoods> findByOrderNo(String orderNo);

    int sumGoodsCountByLimitSale(Long goodsId, Long userId, Date beginTime, Date endTime);
}
