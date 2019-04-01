package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhUserInfo;

/**
 * 用户信息Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhUserInfoRepository extends JpaRepository<PhUserInfo,Long>,JpaSpecificationExecutor<PhUserInfo> {

	PhUserInfo findByPhone(String phone);
	
	PhUserInfo findByQqid(String qqid);
	
	PhUserInfo findByUnionid(String unionid);
	
	PhUserInfo findByWeiboid(String weiboid);
}
