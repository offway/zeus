package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhVoucherInfo;
import java.lang.Long;

/**
 * 优惠券Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhVoucherInfoRepository extends JpaRepository<PhVoucherInfo,Long>,JpaSpecificationExecutor<PhVoucherInfo> {

	List<PhVoucherInfo> findByUserIdOrderByCreateTimeDesc(Long userId);
	
	List<PhVoucherInfo> findByIdInOrderByCreateTimeDesc(List<Long> ids);
	
	@Query(nativeQuery=true,value="select * from ph_voucher_info where user_id=?1 and `status`='0' and type='1' and merchant_id=?2 and NOW() BETWEEN begin_time and end_time and used_min_amount <= ?3")
	List<PhVoucherInfo> findUseByMerchant(Long userId,Long merchantId,Double amount);
	
	@Query(nativeQuery=true,value="select * from ph_voucher_info where user_id=?1 and `status`='0' and type='0' and NOW() BETWEEN begin_time and end_time and used_min_amount <= ?2")
	List<PhVoucherInfo> findUseByPlatform(Long userId,Double amount);
	
	@Query(nativeQuery=true,value="select count(*) from ph_voucher_info where user_id=?1 and `status`='0' and type='1' and merchant_id=?2 and NOW() BETWEEN begin_time and end_time and used_min_amount <= ?3")
	int countUseByMerchant(Long userId,Long merchantId,Double amount);
	
	@Query(nativeQuery=true,value="select count(*) from ph_voucher_info where user_id=?1 and `status`='0' and type='0' and NOW() BETWEEN begin_time and end_time and used_min_amount <= ?2")
	int countUseByPlatform(Long userId,Double amount);
	
}