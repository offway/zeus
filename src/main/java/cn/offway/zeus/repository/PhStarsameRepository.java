package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhStarsame;

/**
 * 明星同款Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhStarsameRepository extends JpaRepository<PhStarsame,Long>,JpaSpecificationExecutor<PhStarsame> {

	@Query(nativeQuery=true,value="select * from ph_starsame order by sort limit 6")
	List<PhStarsame> indexData();
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_starsame set praise_count=praise_count+1 where id=?1")
	int praise(Long id);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_starsame set call_count=call_count+1 where id=?1")
	int addCall(Long id);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_starsame set praise_count=praise_count-1 where id=?1 and praise_count>1")
	int praisecancel(Long id);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_starsame_call set call_count=call_count+1 where starsame_id=?1 and user_id=?2 and DATE_FORMAT(call_time,'%Y-%m-%d') = DATE_FORMAT(NOW(),'%Y-%m-%d') and call_count<3")
	int call(Long starsameId,Long userId);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="insert into ph_starsame_call values(null,?1,?2,1,NOW(),NOW(),NULL)")
	int insertCall(Long starsameId,Long userId);
	
	@Query(nativeQuery=true,value="select count(*) from ph_starsame_call where starsame_id=?1 and user_id=?2 and DATE_FORMAT(call_time,'%Y-%m-%d') = DATE_FORMAT(NOW(),'%Y-%m-%d')")
	int countCall(Long starsameId,Long userId);
	
	@Query(nativeQuery=true,value="select ifnull(sum(ifnull(call_count,0)),0) from ph_starsame_call where starsame_id=?1")
	int countCall(Long starsameId);
	
	
	
}
