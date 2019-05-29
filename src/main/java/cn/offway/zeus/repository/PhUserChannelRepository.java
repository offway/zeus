package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhUserChannel;

/**
 * 用户渠道Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhUserChannelRepository extends JpaRepository<PhUserChannel,Long>,JpaSpecificationExecutor<PhUserChannel> {

	@Query(nativeQuery=true,value="select * from ph_user_channel where phone=?1 order by id desc limit 1")
	PhUserChannel findByPhone(String phone);
}
