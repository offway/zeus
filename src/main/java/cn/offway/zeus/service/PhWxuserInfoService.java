package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhWxuserInfo;

/**
 * 微信用户信息Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhWxuserInfoService{

	PhWxuserInfo save(PhWxuserInfo phWxuserInfo);
	
	PhWxuserInfo getOne(Long id);
	
	PhWxuserInfo findByUnionid(String unionid);
}
