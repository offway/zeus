package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhFreeDeliveryBoost;
import java.lang.Long;
import java.util.List;

/**
 * 免费送助力Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhFreeDeliveryBoostRepository extends JpaRepository<PhFreeDeliveryBoost,Long>,JpaSpecificationExecutor<PhFreeDeliveryBoost> {

	List<PhFreeDeliveryBoost> findByFreeDeliveryUserIdOrderByCreateTimeDesc(Long freedeliveryuserid);

	int countByFreeDeliveryUserIdAndAndBoostUserId(Long freedeliveryuserid,Long bootUserId);
}
