package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhVoucherInfo;
import java.lang.Long;

/**
 * 优惠券Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhVoucherInfoRepository extends JpaRepository<PhVoucherInfo,Long>,JpaSpecificationExecutor<PhVoucherInfo> {

	@Query(nativeQuery=true,value="select * from ph_voucher_info where user_id=?1 and `status`='0'and NOW() BETWEEN begin_time and end_time order by id desc")
	List<PhVoucherInfo> findByUserId(Long userId);
	
	List<PhVoucherInfo> findByIdInOrderByCreateTimeDesc(List<Long> ids);
	
	@Query(nativeQuery=true,value="select * from ph_voucher_info where user_id=?1 and `status`='0' and type='1' and merchant_id=?2 and NOW() BETWEEN begin_time and end_time and used_min_amount <= ?3")
	List<PhVoucherInfo> findUseByMerchant(Long userId,Long merchantId,Double amount);
	
	@Query(nativeQuery=true,value="select * from ph_voucher_info where user_id=?1 and `status`='0' and type='0' and NOW() BETWEEN begin_time and end_time and used_min_amount <= ?2")
	List<PhVoucherInfo> findUseByPlatform(Long userId,Double amount);
	
	@Query(nativeQuery=true,value="select count(*) from ph_voucher_info where user_id=?1 and `status`='0' and type='1' and merchant_id=?2 and NOW() BETWEEN begin_time and end_time and used_min_amount <= ?3")
	int countUseByMerchant(Long userId,Long merchantId,Double amount);
	
	@Query(nativeQuery=true,value="select count(*) from ph_voucher_info where user_id=?1 and `status`='0' and type='0' and NOW() BETWEEN begin_time and end_time and used_min_amount <= ?2")
	int countUseByPlatform(Long userId,Double amount);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_voucher_info set status='1' where id=?1 and user_id=?3 and type='0' and status='0' and used_min_amount<=?2 and NOW() BETWEEN begin_time and end_time")
	int updateStatus(Long voucherId,Double amount,Long userId);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_voucher_info set status='1' where id=?1 and user_id=?4 and type='1' and status='0' and used_min_amount<=?2 and merchant_id=?3 and NOW() BETWEEN begin_time and end_time")
	int updateStatusBym(Long voucherId,Double amount,Long merchant_id,Long userId);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_voucher_info set status='0' where id in(?1) and status='1'")
	int back(List<Long> voucherId);
	
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="insert into ph_voucher_info select null,?1,id,type,`name`,merchant_id,used_min_amount,amount,NOW(),DATE_ADD(NOW(),INTERVAL valid_num DAY),'0',NOW(),NULL,merchant_name from ph_voucher_project where id in(?2)")// and not exists(select 1 from ph_voucher_info where voucher_project_id in(?2))")
	int give(Long userId,List<String> voucherProjectIds);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="insert into ph_voucher_info select null,?1,id,type,`name`,merchant_id,used_min_amount,amount,begin_time,end_time,'0',NOW(),NULL,merchant_name from ph_voucher_project where id in(?2)")// and not exists(select 1 from ph_voucher_info where voucher_project_id in(?2))")
	int giveByTime(Long userId,List<String> voucherProjectIds);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="insert into ph_voucher_info select null,?1,id,type,`name`,merchant_id,used_min_amount,amount,NOW(),DATE_ADD(NOW(),INTERVAL valid_num DAY),'0',NOW(),NULL,merchant_name from ph_voucher_project where id =?2")// and not exists(select 1 from ph_voucher_info where voucher_project_id =?2)")
	int give(Long userId,Long voucherProjectId);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="insert into ph_voucher_info select null,?1,id,type,`name`,merchant_id,used_min_amount,amount,begin_time,end_time,'0',NOW(),NULL,merchant_name from ph_voucher_project where id =?2")// and not exists(select 1 from ph_voucher_info where voucher_project_id =?2)")
	int giveByTime(Long userId,Long voucherProjectId);

	@Query(nativeQuery = true,value = "select count(*) from ph_voucher_info where user_id=?1 and voucher_project_id=?2 and `status`=?3 and NOW() BETWEEN begin_time and end_time")
	int countByUserIdAndVoucherProjectIdAndStatus(Long userId,Long voucherProjectId,String status);
	
	@Query(nativeQuery=true,value="select count(*) from ph_voucher_info where user_id=?1 and `status`='0'and NOW() BETWEEN begin_time and end_time")
	Long countByUserId(Long userId);
	
	@Query(nativeQuery=true,value="select id from ph_voucher_info where user_id=?1 and voucher_project_id=?2 and `status`='0'and NOW() BETWEEN begin_time and end_time order by id desc limit 1")
	Long findId(Long userId,Long voucherProjectId);

	
}