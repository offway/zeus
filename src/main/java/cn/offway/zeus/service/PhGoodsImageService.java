package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhGoodsImage;

/**
 * 商品图片Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhGoodsImageService{

	PhGoodsImage save(PhGoodsImage phGoodsImage);
	
	PhGoodsImage getOne(Long id);

	List<PhGoodsImage> findByGoodsId(Long goodsId);

	List<String> findByGoodsId(Long goodsId, String type);
}
