package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhActivityJoin;
import cn.offway.zeus.dto.ActivityJoin;
import cn.offway.zeus.repository.PhActivityJoinRepository;
import cn.offway.zeus.service.PhActivityJoinService;


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
	
	@Override
	public PhActivityJoin save(PhActivityJoin phActivityJoin){
		return phActivityJoinRepository.save(phActivityJoin);
	}
	
	@Override
	public PhActivityJoin findOne(Long id){
		return phActivityJoinRepository.findOne(id);
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
}
