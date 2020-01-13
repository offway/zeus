package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhChannelUser;

/**
 * 用户推广渠道表Repository接口
 *
 * @author tbw
 * @version $v: 1.0.0, $time:2020-01-13 13:36:31 Exp $
 */
public interface PhChannelUserRepository extends JpaRepository<PhChannelUser,Long>,JpaSpecificationExecutor<PhChannelUser> {

    PhChannelUser findByChannel(String channel);
}
