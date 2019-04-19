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
}
