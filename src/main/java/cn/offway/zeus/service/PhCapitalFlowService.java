package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhCapitalFlow;

/**
 * 资金流水Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhCapitalFlowService{

	PhCapitalFlow save(PhCapitalFlow phCapitalFlow);
	
	PhCapitalFlow findOne(Long id);

	void calculateReturnAmount() throws Exception;
}
