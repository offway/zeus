package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhUserChannel;

/**
 * 用户渠道Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhUserChannelService{

	PhUserChannel save(PhUserChannel phUserChannel);
	
	PhUserChannel findById(Long id);

	PhUserChannel findByPhone(String phone);
}
