package cn.offway.zeus.service.impl;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhOrderExpressInfoService;

import cn.offway.zeus.domain.PhOrderExpressInfo;
import cn.offway.zeus.repository.PhOrderExpressInfoRepository;

import java.util.Optional;


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
	public PhOrderExpressInfo findById(Long id){
		Optional<PhOrderExpressInfo> optional = phOrderExpressInfoRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public PhOrderExpressInfo findByOrderNoAndType(String orderno,String type){
		return phOrderExpressInfoRepository.findByOrderNoAndType(orderno, type);
	}
}
