package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhUserInfoService;

import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.repository.PhUserInfoRepository;


/**
 * 用户信息Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhUserInfoServiceImpl implements PhUserInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhUserInfoRepository phUserInfoRepository;
	
	@Override
	public PhUserInfo save(PhUserInfo phUserInfo){
		return phUserInfoRepository.save(phUserInfo);
	}
	
	@Override
	public PhUserInfo findOne(Long id){
		return phUserInfoRepository.findOne(id);
	}
	
	@Override
	public PhUserInfo findByPhone(String phone){
		return phUserInfoRepository.findByPhone(phone);
	}
	
	@Override
	public PhUserInfo findByQqid(String qqid){
		return phUserInfoRepository.findByQqid(qqid);
	}
	
	@Override
	public PhUserInfo findByUnionid(String unionid){
		return phUserInfoRepository.findByUnionid(unionid);
	}
	
	@Override
	public PhUserInfo findByWeiboid(String weiboid){
		return phUserInfoRepository.findByWeiboid(weiboid);
	}
}
