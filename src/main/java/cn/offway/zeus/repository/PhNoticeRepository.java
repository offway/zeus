package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhNotice;

/**
 * 消息通知Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhNoticeRepository extends JpaRepository<PhNotice,Long>,JpaSpecificationExecutor<PhNotice> {

	@Query(nativeQuery=true,value="select * from ph_notice where id in(select MAX(id) from ph_notice where user_id=?1 GROUP BY type)")
	List<PhNotice> findNoticeIndex(Long userId);
	
	@Query(nativeQuery=true,value="select distinct(type) from ph_notice ")
	List<String> findTypes();
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true,value="update ph_notice set is_read=1 where type=?1")
	int read(String type);
	
	int countByUserIdAndIsRead(Long userId,String isRead);
}
