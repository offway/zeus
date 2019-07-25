package cn.offway.zeus.service;

import java.util.List;
import java.util.Set;

import cn.offway.zeus.domain.PhGoodsStock;

/**
 * 商品库存Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhGoodsStockService{

	PhGoodsStock save(PhGoodsStock phGoodsStock);
	
	PhGoodsStock findById(Long id);

	List<PhGoodsStock> findByGoodsId(Long goodsId);

	List<PhGoodsStock> findByIdIn(Set<Long> ids);

	int updateStock(Long stockId, Long count);

	int sumStock(Long goodsId);
}
