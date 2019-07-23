package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhFreeDeliveryUser;
import java.lang.Long;
import java.util.List;

/**
 * 免费送参与用户Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhFreeDeliveryUserRepository extends JpaRepository<PhFreeDeliveryUser,Long>,JpaSpecificationExecutor<PhFreeDeliveryUser> {

	@Query(nativeQuery=true,value="select * from ph_free_delivery_user where free_delivery_id=?1 order by current_count desc,last_time asc limit 6")
	List<PhFreeDeliveryUser> findByFreeDeliveryId(Long freedeliveryId);
	
	PhFreeDeliveryUser findByFreeDeliveryIdAndUserId(Long freedeliveryId,Long userId);
	
	@Query(nativeQuery=true,value="select * from ph_free_delivery_user where free_delivery_id=?1 order by current_count desc,last_time asc ")
	List<PhFreeDeliveryUser> ranking(Long freedeliveryId);

	@Query(nativeQuery=true,value="select count(id) from ph_free_delivery_user where free_delivery_id=?1 and current_count>=?2")
	int countByFreeDeliveryIdAndCurrentCount(Long freedeliveryId,Long currentCount);

}
