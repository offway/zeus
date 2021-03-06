package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhActivityBlacklist;

/**
 * 活动黑名单表-每日福利Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhActivityBlacklistRepository extends JpaRepository<PhActivityBlacklist,Long>,JpaSpecificationExecutor<PhActivityBlacklist> {

	/** 此处写一些自定义的方法 **/
}
