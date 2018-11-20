package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhActivityJoin;
import cn.offway.zeus.dto.ActivityJoin;

/**
 * 活动参与表-每日福利Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhActivityJoinService{

	PhActivityJoin save(PhActivityJoin phActivityJoin);
	
	PhActivityJoin findOne(Long id);

	int countByUnionidAndActivityId(String unionid, Long activityId);

	List<ActivityJoin> findByunionid(String unionid);
}
