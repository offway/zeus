package cn.offway.zeus.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhProductRule;
import cn.offway.zeus.repository.PhProductRuleRepository;
import cn.offway.zeus.service.PhProductRuleService;


/**
 * 活动规则表Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
@Service
public class PhProductRuleServiceImpl implements PhProductRuleService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhProductRuleRepository phProductRuleRepository;
	
	@Override
	public PhProductRule save(PhProductRule phProductRule){
		return phProductRuleRepository.save(phProductRule);
	}
	
	@Override
	public PhProductRule findOne(Long id){
		return phProductRuleRepository.findOne(id);
	}
	
	@Override
	public List<PhProductRule> findByProductId(Long productId){
		return phProductRuleRepository.findByProductId(productId);
	}
	
	@Override
	public List<String> findContontByProductId(Long productId){
		return phProductRuleRepository.findContontByProductId(productId);
	}
}
