package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhGoodsSpecial;

/**
 * 特殊商品Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhGoodsSpecialService{

	PhGoodsSpecial save(PhGoodsSpecial phGoodsSpecial);
	
	PhGoodsSpecial findById(Long id);

	int countByGoodsId(Long goodsId);
}
