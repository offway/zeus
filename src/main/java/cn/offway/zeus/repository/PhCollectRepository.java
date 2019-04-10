package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import cn.offway.zeus.domain.PhCollect;
import io.swagger.annotations.ApiParam;

/**
 * 收藏夹Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhCollectRepository extends JpaRepository<PhCollect,Long>,JpaSpecificationExecutor<PhCollect> {

	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="insert into ph_collect values(null,?1,?2,?3,now(),null)")
	int insert(Long userId,String type,Long matchId);
}
