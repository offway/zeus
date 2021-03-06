package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhUserInfo;
import com.alipay.api.response.AlipayUserInfoShareResponse;

/**
 * 用户信息Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhUserInfoService{

	PhUserInfo save(PhUserInfo phUserInfo);
	
	PhUserInfo findById(Long id);

	PhUserInfo findByPhone(String phone);

	PhUserInfo findByQqid(String qqid);

	PhUserInfo findByUnionid(String unionid);

	PhUserInfo findByWeiboid(String weiboid);

	int updateCollect(Long id);

	PhUserInfo register(String phone, String unionid, String weiboid, String qqid, String nickName, String headimgurl,
			Long inviteUserId,String channel,String source);

	int subCollect(Long id);

    AlipayUserInfoShareResponse saveAlipayUser(Long userId, String code);

    int addPoints(Long id, Long points);
}
