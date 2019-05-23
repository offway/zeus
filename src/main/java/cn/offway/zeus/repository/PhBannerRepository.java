package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhBanner;

/**
 * Banner管理Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhBannerRepository extends JpaRepository<PhBanner,Long>,JpaSpecificationExecutor<PhBanner> {

	@Query(nativeQuery=true,value="select * from ph_banner where `position` = ?1 and `status`='1' and NOW() BETWEEN begin_time and end_time order by sort")
	List<PhBanner> banners(String position);
}
