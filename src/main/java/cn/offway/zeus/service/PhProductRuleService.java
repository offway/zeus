package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhProductRule;

/**
 * 活动规则表Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhProductRuleService{

	PhProductRule save(PhProductRule phProductRule);
	
	PhProductRule findOne(Long id);

	List<PhProductRule> findByProductId(Long productId);

	List<String> findContontByProductId(Long productId);
}
