package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhActivityPrize;

/**
 * 活动奖品表-每日福利Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhActivityPrizeService{

	PhActivityPrize save(PhActivityPrize phActivityPrize);
	
	PhActivityPrize findById(Long id);

	PhActivityPrize findByActivityIdAndUnionid(Long activityid, String unionid);

	/**
	 * 开奖
	 * @param activityId
	 */
	void open(Long activityId);

	List<PhActivityPrize> findByActivityId(Long activityid);
}
