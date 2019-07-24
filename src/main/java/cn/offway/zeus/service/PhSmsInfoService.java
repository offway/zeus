package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhSmsInfo;

/**
 * 短信记录Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhSmsInfoService{

	PhSmsInfo save(PhSmsInfo phSmsInfo);
	
	PhSmsInfo getOne(Long id);

	int countByIp(String ip);
}
