package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhFreeDeliveryUser;

/**
 * 免费送参与用户Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhFreeDeliveryUserService{

	PhFreeDeliveryUser save(PhFreeDeliveryUser phFreeDeliveryUser);
	
	PhFreeDeliveryUser findOne(Long id);

	List<PhFreeDeliveryUser> findByFreeDeliveryId(Long freedeliveryId);

	PhFreeDeliveryUser findByFreeDeliveryIdAndUserId(Long freedeliveryId, Long userId);
	
	List<PhFreeDeliveryUser> ranking(Long freedeliveryId);

	int countByFreeDeliveryIdAndCurrentCount(Long freedeliveryId,Long currentCount);
}
