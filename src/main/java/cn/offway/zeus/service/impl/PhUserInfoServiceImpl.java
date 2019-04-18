package cn.offway.zeus.service.impl;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.service.PhConfigService;
import cn.offway.zeus.service.PhInviteInfoService;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.service.PhVoucherInfoService;
import cn.offway.zeus.domain.PhInviteInfo;
import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.exception.StockException;
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
	
	@Autowired
	private PhInviteInfoService phInviteInfoService;
	
	@Autowired
	private PhVoucherInfoService phVoucherInfoService;
	
	@Autowired
	private PhConfigService phConfigService;
	
	
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
	
	@Override
	public int updateCollect(Long id){
		return phUserInfoRepository.updateCollect(id);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {Exception.class,StockException.class})
	public PhUserInfo register(String phone, String unionid, String weiboid, String qqid, String nickName,
			String headimgurl, Long inviteUserId) {
		PhUserInfo phUserInfo = new PhUserInfo();
		phUserInfo.setPhone(phone);
		if(StringUtils.isNotBlank(phone)){
			nickName = StringUtils.isBlank(nickName)?"OFFWAY_"+phone.substring(5):nickName;
		}
		phUserInfo.setNickname(nickName);
		phUserInfo.setHeadimgurl(headimgurl);
		phUserInfo.setWeiboid(weiboid);
		phUserInfo.setQqid(qqid);
		phUserInfo.setUnionid(unionid);
		phUserInfo.setBalance(0D);
		phUserInfo.setSex("1");
		phUserInfo.setVersion(0L);
		phUserInfo.setVoucherCount(0L);
		phUserInfo.setCollectCount(0L);
		phUserInfo.setCreateTime(new Date());
		phUserInfo = save(phUserInfo);
		
		if(null != inviteUserId){
			PhUserInfo inviteUserInfo = findOne(inviteUserId);
			if(null!= inviteUserInfo){
				PhInviteInfo phInviteInfo = new PhInviteInfo();
				phInviteInfo.setUserId(inviteUserId);
				phInviteInfo.setNickname(inviteUserInfo.getNickname());
				phInviteInfo.setPhone(inviteUserInfo.getPhone());
				phInviteInfo.setInviteUserId(phUserInfo.getId());
				phInviteInfo.setCreateTime(new Date());
				phInviteInfoService.save(phInviteInfo);
			}
		}
		//赠送优惠券大礼包
		String content = phConfigService.findContentByName("VP_REGISTER");
		phVoucherInfoService.give(phUserInfo.getId(), Arrays.asList(content.split(",")));
		
		return phUserInfo;
	}
}
