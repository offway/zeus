package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhActivityImage;

/**
 * 活动图片表-每日福利Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhActivityImageService{

	PhActivityImage save(PhActivityImage phActivityImage);
	
	PhActivityImage getOne(Long id);

	List<PhActivityImage> findByActivityId(Long activityId);
}
