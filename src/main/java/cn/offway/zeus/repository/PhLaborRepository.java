package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhLabor;
import java.lang.Long;
import java.util.List;

/**
 * 劳动节活动Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhLaborRepository extends JpaRepository<PhLabor,Long>,JpaSpecificationExecutor<PhLabor> {

	int countByUserIdAndSignDate(Long userId,String signDate);
	
	PhLabor findByUserId(Long userId);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_labor set lottery_num = lottery_num-1 where user_id=?1 and lottery_num>0 ")
	int subLotteryNum(Long userId);

}
