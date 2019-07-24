package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhActivityInfo;
import cn.offway.zeus.domain.PhActivityJoin;
import cn.offway.zeus.domain.PhWxuserInfo;
import cn.offway.zeus.dto.ActivityJoin;

/**
 * 活动参与表-每日福利Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhActivityJoinService{

	PhActivityJoin save(PhActivityJoin phActivityJoin);
	
	PhActivityJoin getOne(Long id);

	int countByUnionidAndActivityId(String unionid, Long activityId);

	List<ActivityJoin> findByunionid(String unionid);

	List<PhActivityJoin> findByActivityId(Long activityId);

	List<PhActivityJoin> luckly(Long activityId, Long num);

	int updateLuckly(List<Long> ids);

	void join(PhActivityInfo phActivityInfo, PhWxuserInfo phWxuserInfo, String formId, String distinctId);
}
