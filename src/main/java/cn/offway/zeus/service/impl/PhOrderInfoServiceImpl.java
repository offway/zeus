package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhOrderInfoService;

import cn.offway.zeus.domain.PhOrderInfo;
import cn.offway.zeus.repository.PhOrderInfoRepository;


/**
 * 订单Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhOrderInfoServiceImpl implements PhOrderInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhOrderInfoRepository phOrderInfoRepository;
	
	@Override
	public PhOrderInfo save(PhOrderInfo phOrderInfo){
		return phOrderInfoRepository.save(phOrderInfo);
	}
	
	@Override
	public PhOrderInfo findOne(Long id){
		return phOrderInfoRepository.findOne(id);
	}
}
