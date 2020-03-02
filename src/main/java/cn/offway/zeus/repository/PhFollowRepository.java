package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhFollow;

/**
 * 关注列表Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2020-03-02 13:55:02 Exp $
 */
public interface PhFollowRepository extends JpaRepository<PhFollow,Long>,JpaSpecificationExecutor<PhFollow> {

	/** 此处写一些自定义的方法 **/
}
