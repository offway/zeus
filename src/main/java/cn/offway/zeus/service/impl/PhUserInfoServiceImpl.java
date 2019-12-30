package cn.offway.zeus.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import cn.offway.zeus.domain.PhMerchant;
import cn.offway.zeus.service.*;
import cn.offway.zeus.utils.JsonResult;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.dto.GIOEventMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhInviteInfo;
import cn.offway.zeus.domain.PhUserChannel;
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
	
	@Autowired
	private PhUserChannelService phUserChannelService;

	@Autowired
	private AlipayService alipayService;
	
	
	@Override
	public PhUserInfo save(PhUserInfo phUserInfo){
		return phUserInfoRepository.save(phUserInfo);
	}
	
	@Override
	public PhUserInfo findById(Long id){
		Optional<PhUserInfo> optional = phUserInfoRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
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
	public int subCollect(Long id){
		return phUserInfoRepository.subCollect(id);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {Exception.class,StockException.class})
	public PhUserInfo register(String phone, String unionid, String weiboid, String qqid, String nickName,
			String headimgurl, Long inviteUserId,String channel,String source) {
		PhUserInfo phUserInfo = new PhUserInfo();
		phUserInfo.setPhone(phone);
		if(StringUtils.isNotBlank(phone)){
			nickName = StringUtils.isBlank(nickName)?"OFFWAY_"+phone.substring(5):nickName;
		}
		if (StringUtils.isNotBlank(source)){
			phUserInfo.setSource(source);
		}else {
			phUserInfo.setSource("4");
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
		phUserInfo.setChannel(channel);
		phUserInfo.setCreateTime(new Date());
		phUserInfo.setPoints(0L);
		phUserInfo.setSignCount(0L);
		phUserInfo = save(phUserInfo);
		
		String content = phConfigService.findContentByName("VP_REGISTER");
				
		if(null != inviteUserId){
			PhUserInfo inviteUserInfo = findById(inviteUserId);
			if(null!= inviteUserInfo){
				PhInviteInfo phInviteInfo = new PhInviteInfo();
				phInviteInfo.setUserId(inviteUserId);
				phInviteInfo.setNickname(inviteUserInfo.getNickname());
				phInviteInfo.setPhone(inviteUserInfo.getPhone());
				phInviteInfo.setInviteUserId(phUserInfo.getId());
				phInviteInfo.setCreateTime(new Date());
				phInviteInfoService.save(phInviteInfo);
				
				//查询是否第一次邀请
				int count = phInviteInfoService.countByUserId(inviteUserId);
				if(count == 1){
					//赠送优惠券大礼包
					phVoucherInfoService.give(inviteUserId, Arrays.asList(content.split(",")));
				}
				
				
			}
		}
		//赠送优惠券大礼包
		phVoucherInfoService.give(phUserInfo.getId(), Arrays.asList(content.split(",")));
		
		/*//查询渠道
		PhUserChannel phUserChannel = phUserChannelService.findByPhone(phone);
		if(null != phUserChannel){
			phUserInfo.setChannel(phUserChannel.getChannel());
		}*/
		GIOEventMessage eventMessage = new GIOEventMessage.Builder()
				.eventTime(System.currentTimeMillis())            // 事件时间，默认为系统时间（选填）
				.eventKey("goodsDetails")                           // 事件标识 (必填)
				.loginUserId(phUserInfo.getId().toString())                   // 登录用户ID (必填)
				.build();
		//上传事件行为消息到服务器
		GrowingAPI.send(eventMessage);
		
		return phUserInfo;
	}

	@Override
	public AlipayUserInfoShareResponse saveAlipayUser(Long userId, String code){
		String accessToken = alipayService.oauthToken(userId, code);
		AlipayUserInfoShareResponse alipayUserInfoShareResponse = alipayService.userInfoShare(accessToken);
		phUserInfoRepository.updateAlipayUserId(userId,alipayUserInfoShareResponse.getUserId(),alipayUserInfoShareResponse.getNickName());
		return alipayUserInfoShareResponse;
	}

	@Override
	public int addPoints(Long id,Long points){
		return phUserInfoRepository.addPoints(id,points);
	}
}
