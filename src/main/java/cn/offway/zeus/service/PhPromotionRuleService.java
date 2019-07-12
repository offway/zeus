package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhPromotionRule;

/**
 * 促销活动规则Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhPromotionRuleService{

	PhPromotionRule save(PhPromotionRule phPromotionRule);
	
	PhPromotionRule findOne(Long id);
}
