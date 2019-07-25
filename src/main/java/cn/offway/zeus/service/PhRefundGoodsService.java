package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhRefundGoods;

/**
 * 退款/退货商品明细Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhRefundGoodsService{

	PhRefundGoods save(PhRefundGoods phRefundGoods);
	
	PhRefundGoods findById(Long id);

	List<PhRefundGoods> save(List<PhRefundGoods> phRefundGoodss);
	
	Long refundGoodsCount(Long orderGoodsId);

	int deleteByRefundId(Long refundId);
	
	List<PhRefundGoods> findByRefundId(Long refundId);
}
