package cn.offway.zeus.service.impl;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhSmsInfoService;

import cn.offway.zeus.domain.PhSmsInfo;
import cn.offway.zeus.repository.PhSmsInfoRepository;

import java.util.Optional;


/**
 * 短信记录Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhSmsInfoServiceImpl implements PhSmsInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhSmsInfoRepository phSmsInfoRepository;
	
	@Override
	public PhSmsInfo save(PhSmsInfo phSmsInfo){
		return phSmsInfoRepository.save(phSmsInfo);
	}
	
	@Override
	public PhSmsInfo findById(Long id){
		Optional<PhSmsInfo> optional = phSmsInfoRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public int countByIp(String ip){
		return phSmsInfoRepository.countByIp(ip);
	}
}
