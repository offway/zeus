package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhLaborPrize;
import java.lang.String;
import java.util.List;

/**
 * 劳动节活动奖品Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhLaborPrizeRepository extends JpaRepository<PhLaborPrize,Long>,JpaSpecificationExecutor<PhLaborPrize> {

	
	@Query(nativeQuery = true,value="select * from ph_labor_prize where type=?1 and `status`='0' ORDER BY RAND() limit 1")
	PhLaborPrize lottery(String type);
	
	PhLaborPrize findByTypeAndStatusAndRemark(String type,String status,String remark);
}
