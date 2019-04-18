package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.service.PhCapitalFlowService;

import cn.offway.zeus.domain.PhCapitalFlow;
import cn.offway.zeus.exception.StockException;
import cn.offway.zeus.repository.PhCapitalFlowRepository;
import cn.offway.zeus.repository.PhOrderInfoRepository;


/**
 * 资金流水Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhCapitalFlowServiceImpl implements PhCapitalFlowService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhCapitalFlowRepository phCapitalFlowRepository;
	
	
	@Override
	public PhCapitalFlow save(PhCapitalFlow phCapitalFlow){
		return phCapitalFlowRepository.save(phCapitalFlow);
	}
	
	@Override
	public PhCapitalFlow findOne(Long id){
		return phCapitalFlowRepository.findOne(id);
	}
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {Exception.class,StockException.class})
	public void calculateReturnAmount() throws Exception{
		
		phCapitalFlowRepository.insertByReturnAmount();
		phCapitalFlowRepository.updateBalanceByReturnAmount();
	}
}
