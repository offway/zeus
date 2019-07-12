package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhPromotionInfo;

/**
 * 促销活动Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhPromotionInfoService{

	PhPromotionInfo save(PhPromotionInfo phPromotionInfo);
	
	PhPromotionInfo findOne(Long id);
}
