package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhActivityJoin;
import cn.offway.zeus.dto.ActivityJoin;
import java.lang.Long;

/**
 * 活动参与表-每日福利Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhActivityJoinRepository extends JpaRepository<PhActivityJoin,Long>,JpaSpecificationExecutor<PhActivityJoin> {

	 int countByUnionidAndActivityId(String unionid,Long activityId);
	 
	 @Query(nativeQuery = true, value="select i.id,i.name,i.image,j.create_time,i.end_time from ph_activity_join j ,ph_activity_info i where i.id = j.activity_id and j.unionid=?1 order by j.create_time desc limit 15")
	 List<Object> findByUnionid(String unionid);
	 
	 List<PhActivityJoin> findByActivityId(Long activityId);
	 
	 @Query(nativeQuery=true,value="select aj.* from ph_activity_join aj where aj.activity_id=?1 and  not EXISTS (select 1 from ph_activity_blacklist ab where ab.unionid = aj.unionid) and not exists (select 1 from ph_activity_prize ap where ap.unionid = aj.unionid) order by RAND() limit ?2")
	 List<PhActivityJoin> luckly(Long activityId,Long num);
	 
	 @Transactional
	 @Modifying
	 @Query(nativeQuery=true,value="update ph_activity_join set is_lucky='1' where id in(?1) ")
	 int updateLuckly(List<Long> ids);
	 
	 
}
