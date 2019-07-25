package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhGoodsProperty;

/**
 * 商品属性Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhGoodsPropertyService{

	PhGoodsProperty save(PhGoodsProperty phGoodsProperty);
	
	PhGoodsProperty findById(Long id);
	
	List<PhGoodsProperty> findByGoodsId(Long goodsId);

	List<PhGoodsProperty> findByGoodsStockIdOrderBySortAsc(Long goodsStockId);
}
