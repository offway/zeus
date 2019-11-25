package cn.offway.zeus.service.impl;

import cn.offway.zeus.domain.PhActivityInfo;
import cn.offway.zeus.domain.PhActivityJoin;
import cn.offway.zeus.domain.PhWxuserInfo;
import cn.offway.zeus.dto.ActivityJoin;
import cn.offway.zeus.repository.PhActivityJoinRepository;
import cn.offway.zeus.service.PhActivityInfoService;
import cn.offway.zeus.service.PhActivityJoinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * 活动参与表-每日福利Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
@Service
public class PhActivityJoinServiceImpl implements PhActivityJoinService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhActivityJoinRepository phActivityJoinRepository;
	
	@Autowired
	private PhActivityInfoService phActivityInfoService;
	
	@Override
	public PhActivityJoin save(PhActivityJoin phActivityJoin){
		return phActivityJoinRepository.save(phActivityJoin);
	}
	
	@Override
	public PhActivityJoin findById(Long id){
		Optional<PhActivityJoin> optional = phActivityJoinRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public int countByUnionidAndActivityId(String unionid,Long activityId){
		return phActivityJoinRepository.countByUnionidAndActivityId(unionid, activityId);
	}
	
	@Override
	public List<ActivityJoin> findByunionid(String unionid){
		List<ActivityJoin> activityJoins = new ArrayList<>();
		List<Object> objects = phActivityJoinRepository.findByUnionid(unionid);
		for (Object object : objects) {
			Object[] obj = (Object[])object;
			activityJoins.add(new ActivityJoin(Long.valueOf(obj[0].toString()), obj[1].toString(), obj[2].toString(), (Date)obj[3], (Date)obj[4]));
		}
		return activityJoins;
	}
	
	@Override
	public void join(PhActivityInfo phActivityInfo,PhWxuserInfo phWxuserInfo,String formId,String distinctId){
		
		
		PhActivityJoin phActivityJoin = new PhActivityJoin();
		phActivityJoin.setActivityId(phActivityInfo.getId());
		phActivityJoin.setActivityImage(phActivityInfo.getImage());
		phActivityJoin.setActivityName(phActivityInfo.getName());
		phActivityJoin.setCreateTime(new Date());
		phActivityJoin.setHeadUrl(phWxuserInfo.getHeadimgurl());
		phActivityJoin.setIsLucky("0");
		phActivityJoin.setNickName(phWxuserInfo.getNickname());
		phActivityJoin.setRemark("");
		phActivityJoin.setFormId(formId);
		phActivityJoin.setUnionid(phWxuserInfo.getUnionid());
		
		save(phActivityJoin);
		
		phActivityInfo.setJoinNum(phActivityInfo.getJoinNum().longValue()+1L);
		phActivityInfoService.save(phActivityInfo);
		
		saTrack(phWxuserInfo.getUnionid(),distinctId, phActivityInfo.getId(), phActivityInfo.getName());
		
		
	}

	private void saTrack(String unionid, String distinctId, Long activityId, String activityName) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("activity_name", activityName);
		properties.put("channel", "小程序");
		properties.put("activity_id", activityId);
	}
	
	@Override
	public List<PhActivityJoin> findByActivityId(Long activityId){
		return phActivityJoinRepository.findByActivityId(activityId);
	}
	
	@Override
	public List<PhActivityJoin> luckly(Long activityId,Long num){
		return phActivityJoinRepository.luckly(activityId, num);
	}
	
	@Override
	public int updateLuckly(List<Long> ids){
		return phActivityJoinRepository.updateLuckly(ids);
	}
}
