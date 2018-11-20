package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhActivityPrize;

/**
 * 活动奖品表-每日福利Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhActivityPrizeRepository extends JpaRepository<PhActivityPrize,Long>,JpaSpecificationExecutor<PhActivityPrize> {

	/** 此处写一些自定义的方法 **/
}
