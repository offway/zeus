package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhSmsInfoService;

import cn.offway.zeus.domain.PhSmsInfo;
import cn.offway.zeus.repository.PhSmsInfoRepository;


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
	public PhSmsInfo getOne(Long id){
		return phSmsInfoRepository.getOne(id);
	}
	
	@Override
	public int countByIp(String ip){
		return phSmsInfoRepository.countByIp(ip);
	}
}
