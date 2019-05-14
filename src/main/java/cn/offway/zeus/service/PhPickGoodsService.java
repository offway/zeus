package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhPickGoods;

/**
 * OFFWAY优选商品明细Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhPickGoodsService{

	PhPickGoods save(PhPickGoods phPickGoods);
	
	PhPickGoods findOne(Long id);
}
