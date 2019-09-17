package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhGoodsType;

import java.util.List;

/**
 * 商品类别Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhGoodsTypeService{

	PhGoodsType save(PhGoodsType phGoodsType);
	
	PhGoodsType findById(Long id);

	List<PhGoodsType> findAll();
}
