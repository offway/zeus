package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhAddress;


/**
 * 地址管理Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-02-12 11:26:00 Exp $
 */
public interface PhAddressRepository extends JpaRepository<PhAddress,Long>,JpaSpecificationExecutor<PhAddress> {

	@Query(nativeQuery=true,value="select * from  ph_address where user_id =?1 order by is_default desc")
	List<PhAddress> findByUserId(Long userId);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_address set is_default='0' where user_id =?1")
	int updatePhAddress(Long userId);
	
	List<PhAddress> findByUserIdAndIsDefault(Long userId,String isDefault);
	
	int countByIdAndUserId(Long id,Long userId);
}
