package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhPreorderInfo;

/**
 * 预生成订单Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhPreorderInfoRepository extends JpaRepository<PhPreorderInfo,Long>,JpaSpecificationExecutor<PhPreorderInfo> {

	PhPreorderInfo findByOrderNoAndStatus(String orderno,String status);
	
	int countByUserIdAndStatus(Long userId,String status);
	
	@Query(nativeQuery=true,value="select order_no from ph_preorder_info where status ='0' and create_time<= DATE_SUB(NOW(),INTERVAL 30 MINUTE)")
	List<String> orderTimeOut();
	
	PhPreorderInfo findByOrderNo(String orderno);
}
