package cn.offway.zeus.service;


import java.util.List;

import cn.offway.zeus.domain.PhRefundOrderGoods;

/**
 * 退换货后的订单商品Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-08-01 14:42:53 Exp $
 */
public interface PhRefundOrderGoodsService{

    PhRefundOrderGoods save(PhRefundOrderGoods phRefundOrderGoods);
	
    PhRefundOrderGoods findById(Long id);

    void delete(Long id);

    List<PhRefundOrderGoods> saveAll(List<PhRefundOrderGoods> entities);
}
