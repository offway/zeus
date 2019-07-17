package cn.offway.zeus.service;


import java.util.List;

import cn.offway.zeus.domain.PhPromotionRule;

/**
 * 促销活动规则Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-07-12 17:38:42 Exp $
 */
public interface PhPromotionRuleService{

    PhPromotionRule save(PhPromotionRule phPromotionRule);
	
    PhPromotionRule findOne(Long id);

    void delete(Long id);

    List<PhPromotionRule> save(List<PhPromotionRule> entities);

    PhPromotionRule findByPromotionIdAnAndReduceLimit(Long promotionId,Double goodsAmount);

    PhPromotionRule findByPromotionIdAndDiscountNum(Long promotionId,int discountNum);

    List<PhPromotionRule> findByPlatform(List<Long> goodsIds);
}
