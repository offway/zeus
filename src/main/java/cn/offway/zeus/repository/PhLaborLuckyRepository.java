package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhLaborLucky;
import java.lang.Long;
import java.util.List;

/**
 * 劳动节活动中奖记录Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhLaborLuckyRepository extends JpaRepository<PhLaborLucky,Long>,JpaSpecificationExecutor<PhLaborLucky> {

	List<PhLaborLucky> findByUserIdOrderByCreateTimeDesc(Long userId);
	
	@Query(nativeQuery=true,value="select * from ph_labor_lucky order by create_time desc limit 99")
	List<PhLaborLucky> findByOrderByCreateTimeDesc();
}
