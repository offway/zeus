package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhAccumulatePoints;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 积分记录Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-09-03 14:17:13 Exp $
 */
public interface PhAccumulatePointsRepository extends JpaRepository<PhAccumulatePoints, Long>, JpaSpecificationExecutor<PhAccumulatePoints> {

    @Query(nativeQuery = true, value = "select count(id) from ph_accumulate_points where user_id=?1 and DATE_FORMAT(`create_time` ,'%Y-%M-%d') = DATE_FORMAT(NOW(),'%Y-%M-%d')")
    int countByUserIdToday(Long userId);

    @Query(nativeQuery = true, value = "select type,count(id) from ph_accumulate_points where user_id = ?1 and type in ('1','2','3') and DATE_FORMAT(`create_time` ,'%Y-%M-%d') = DATE_FORMAT(NOW(),'%Y-%M-%d') group by type")
    List<Object> pointsByUserIdAndToday(Long userId);

    @Query(nativeQuery = true, value = "select create_time from ph_accumulate_points where type='0' and user_id=?1 order by create_time desc limit 1")
    Date endSignTime(Long userId);

    @Query(nativeQuery = true, value = "select count(*) from ph_accumulate_points where user_id = ?1 and TO_DAYS(create_time)  = TO_DAYS(NOW()) and type = ?2")
    int findByUseridNoW(Long userid,String type);
}
