package cn.offway.zeus.service;


import java.util.List;

import cn.offway.zeus.domain.PhPromotionGoods;

/**
 * 促销活动对应商品Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-07-12 17:38:42 Exp $
 */
public interface PhPromotionGoodsService{

    PhPromotionGoods save(PhPromotionGoods phPromotionGoods);
	
    PhPromotionGoods findOne(Long id);

    void delete(Long id);

    List<PhPromotionGoods> save(List<PhPromotionGoods> entities);
}
