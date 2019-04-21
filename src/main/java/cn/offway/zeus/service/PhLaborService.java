package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhLabor;
import cn.offway.zeus.utils.JsonResult;

/**
 * 劳动节活动Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhLaborService{

	PhLabor save(PhLabor phLabor);
	
	PhLabor findOne(Long id);

	void sign(Long userId);

	PhLabor findByUserId(Long userId);

	JsonResult lottery(Long userId) throws Exception;
}
