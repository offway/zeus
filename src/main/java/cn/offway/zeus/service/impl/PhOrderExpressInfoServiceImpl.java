package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhOrderExpressInfoService;

import cn.offway.zeus.domain.PhOrderExpressInfo;
import cn.offway.zeus.repository.PhOrderExpressInfoRepository;


/**
 * 订单物流Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhOrderExpressInfoServiceImpl implements PhOrderExpressInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhOrderExpressInfoRepository phOrderExpressInfoRepository;
	
	@Override
	public PhOrderExpressInfo save(PhOrderExpressInfo phOrderExpressInfo){
		return phOrderExpressInfoRepository.save(phOrderExpressInfo);
	}
	
	@Override
	public PhOrderExpressInfo findOne(Long id){
		return phOrderExpressInfoRepository.findOne(id);
	}
	
	@Override
	public PhOrderExpressInfo findByOrderNoAndType(String orderno,String type){
		return phOrderExpressInfoRepository.findByOrderNoAndType(orderno, type);
	}
}
