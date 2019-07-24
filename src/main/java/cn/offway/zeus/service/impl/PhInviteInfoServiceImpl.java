package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhInviteInfoService;

import cn.offway.zeus.domain.PhInviteInfo;
import cn.offway.zeus.repository.PhInviteInfoRepository;


/**
 * 邀请记录表Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhInviteInfoServiceImpl implements PhInviteInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhInviteInfoRepository phInviteInfoRepository;
	
	@Override
	public PhInviteInfo save(PhInviteInfo phInviteInfo){
		return phInviteInfoRepository.save(phInviteInfo);
	}
	
	@Override
	public PhInviteInfo getOne(Long id){
		return phInviteInfoRepository.getOne(id);
	}
	
	@Override
	public int countByUserId(Long userId){
		return phInviteInfoRepository.countByUserId(userId);
	}
}
