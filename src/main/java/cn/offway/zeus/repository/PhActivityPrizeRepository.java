package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhActivityPrize;
import java.lang.Long;

/**
 * 活动奖品表-每日福利Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhActivityPrizeRepository extends JpaRepository<PhActivityPrize,Long>,JpaSpecificationExecutor<PhActivityPrize> {

	PhActivityPrize findByActivityIdAndUnionid(Long activityid,String unionid);
	
	List<PhActivityPrize> findByActivityId(Long activityid);
}
