package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhWxuserInfo;
import cn.offway.zeus.repository.PhWxuserInfoRepository;
import cn.offway.zeus.service.PhWxuserInfoService;


/**
 * 微信用户信息Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
@Service
public class PhWxuserInfoServiceImpl implements PhWxuserInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhWxuserInfoRepository phWxuserInfoRepository;
	
	@Override
	public PhWxuserInfo save(PhWxuserInfo phWxuserInfo){
		return phWxuserInfoRepository.save(phWxuserInfo);
	}
	
	@Override
	public PhWxuserInfo findOne(Long id){
		return phWxuserInfoRepository.findOne(id);
	}

	@Override
	public PhWxuserInfo findByUnionid(String unionid){
		return phWxuserInfoRepository.findByUnionid(unionid);
	}
}
