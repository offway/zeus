package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhInviteInfo;

/**
 * 邀请记录表Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhInviteInfoRepository extends JpaRepository<PhInviteInfo,Long>,JpaSpecificationExecutor<PhInviteInfo> {

	/** 此处写一些自定义的方法 **/
}
