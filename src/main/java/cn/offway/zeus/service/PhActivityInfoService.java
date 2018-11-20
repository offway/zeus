package cn.offway.zeus.service;

import java.util.List;
import java.util.Map;

import cn.offway.zeus.domain.PhActivityInfo;

/**
 * 活动表-每日福利Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhActivityInfoService{

	PhActivityInfo save(PhActivityInfo phActivityInfo);
	
	PhActivityInfo findOne(Long id);

	Map<String, List<PhActivityInfo>> list();

	Map<String, Object> detail(Long activityId, String unionid);
}
