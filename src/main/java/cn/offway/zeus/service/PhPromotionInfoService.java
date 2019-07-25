package cn.offway.zeus.service;


import java.util.List;

import cn.offway.zeus.domain.PhPromotionInfo;

/**
 * 促销活动Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-07-12 17:38:42 Exp $
 */
public interface PhPromotionInfoService{

    PhPromotionInfo save(PhPromotionInfo phPromotionInfo);
	
    PhPromotionInfo findById(Long id);

    void deleteById(Long id);

    List<PhPromotionInfo> save(List<PhPromotionInfo> entities);

    List<PhPromotionInfo> findByMerchantIdAndGoodsId(Long merchantId,List<Long> goodsIds);

    List<PhPromotionInfo> findByPlatformAndGoodsId(List<Long> goodsIds);
}
