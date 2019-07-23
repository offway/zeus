package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhFreeDeliveryBoost;

/**
 * 免费送助力Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhFreeDeliveryBoostService{

	PhFreeDeliveryBoost save(PhFreeDeliveryBoost phFreeDeliveryBoost);
	
	PhFreeDeliveryBoost findOne(Long id);

	int countByFreeDeliveryUserIdAndAndBoostUserId(Long freedeliveryuserid,Long bootUserId);
}
