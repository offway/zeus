package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhInviteInfo;

/**
 * 邀请记录表Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhInviteInfoService{

	PhInviteInfo save(PhInviteInfo phInviteInfo);
	
	PhInviteInfo findOne(Long id);

	int countByUserId(Long userId);
}
