package cn.offway.zeus.service;

import cn.offway.zeus.utils.JsonResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.offway.zeus.domain.PhFreeDelivery;

/**
 * 免费送Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhFreeDeliveryService{

	PhFreeDelivery save(PhFreeDelivery phFreeDelivery);
	
	PhFreeDelivery findById(Long id);

	Page<PhFreeDelivery> findByPage(Long productId,Pageable page);

	JsonResult boost(Long freeDeliveryId, Long userId, Long boostUserId) throws Exception;
}
