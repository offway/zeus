package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhInviteInfo;

/**
 * 邀请记录表Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhInviteInfoRepository extends JpaRepository<PhInviteInfo,Long>,JpaSpecificationExecutor<PhInviteInfo> {

	@Query(nativeQuery=true,value="select u.headimgurl,u.nickname from ph_invite_info i,ph_user_info u where i.user_id=?1 and i.invite_user_id= u.id order by i.create_time desc")
	List<Object> invites(Long userId);
	
	int countByUserId(Long userId);
}
