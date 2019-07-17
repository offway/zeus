package cn.offway.zeus.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhPromotionRuleService;

import cn.offway.zeus.domain.PhPromotionRule;
import cn.offway.zeus.repository.PhPromotionRuleRepository;


/**
 * 促销活动规则Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-07-12 17:38:42 Exp $
 */
@Service
public class PhPromotionRuleServiceImpl implements PhPromotionRuleService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhPromotionRuleRepository phPromotionRuleRepository;
	
	@Override
	public PhPromotionRule save(PhPromotionRule phPromotionRule){
		return phPromotionRuleRepository.save(phPromotionRule);
	}
	
	@Override
	public PhPromotionRule findOne(Long id){
		return phPromotionRuleRepository.findOne(id);
	}

	@Override
	public void delete(Long id){
		phPromotionRuleRepository.delete(id);
	}

	@Override
	public List<PhPromotionRule> save(List<PhPromotionRule> entities){
		return phPromotionRuleRepository.save(entities);
	}

	@Override
	public PhPromotionRule findByPromotionIdAnAndReduceLimit(Long promotionId, Double goodsAmount) {
		return phPromotionRuleRepository.findByPromotionIdAnAndReduceLimit(promotionId,goodsAmount);
	}

	@Override
	public PhPromotionRule findByPromotionIdAndDiscountNum(Long promotionId, int discountNum) {
		return phPromotionRuleRepository.findByPromotionIdAndDiscountNum(promotionId,discountNum);
	}

	@Override
	public List<PhPromotionRule> findByPlatform(List<Long> goodsIds) {
		return phPromotionRuleRepository.findByPlatform(goodsIds);
	}
}
