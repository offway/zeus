package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhGoodsCategory;

/**
 * 商品类目Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhGoodsCategoryService{

	PhGoodsCategory save(PhGoodsCategory phGoodsCategory);
	
	PhGoodsCategory findOne(Long id);
}
