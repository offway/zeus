package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhOrderInfo;

/**
 * 订单Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhOrderInfoRepository extends JpaRepository<PhOrderInfo,Long>,JpaSpecificationExecutor<PhOrderInfo> {

	@Query(nativeQuery=true,value="select CONCAT(?1,LPAD(nextval(?1), 6, 0))")
	String nextval(String prefix);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true,value="insert into sequence VALUES(?1,0,1)")
	int sequence(String prefix);
	
	@Query(nativeQuery=true,value="select count(*) from sequence where name=?1 ")
	int countSequence(String prefix);
	
}