package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhConfig;

/**
 * 配置Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhConfigRepository extends JpaRepository<PhConfig,Long>,JpaSpecificationExecutor<PhConfig> {

	@Query(nativeQuery=true,value="select content from ph_config where name=?1")
	String findContentByName(String name);

	PhConfig findByName(String name);
	
	List<PhConfig> findByNameIn(String... names);
}
