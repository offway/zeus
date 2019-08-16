package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhVoucherInfo;
import cn.offway.zeus.domain.PhVoucherProject;
import java.lang.Long;
import java.util.List;

/**
 * 优惠券方案Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhVoucherProjectRepository extends JpaRepository<PhVoucherProject,Long>,JpaSpecificationExecutor<PhVoucherProject> {

	@Query(nativeQuery = true,value = "select * from ph_voucher_project where id in(?1)")
	List<PhVoucherProject> findByIdIn(List<String> ids);
	
	@Query(nativeQuery=true,value="select * from ph_voucher_project where type='1' and merchant_id=?1 and (NOW() BETWEEN begin_time and end_time or valid_num is not null)")
	List<PhVoucherProject> findByMerchantId(Long merchantId);

	@Query(nativeQuery=true,value="select * from ph_voucher_project where type='1' and merchant_id=?1 and is_private='0' and (NOW() BETWEEN begin_time and end_time or valid_num is not null)")
	List<PhVoucherProject> findByMerchantIdPublic(Long merchantId);
	
	@Query(nativeQuery=true,value="select * from ph_voucher_project where type='1' and merchant_id=?1 and is_private='0' and NOW() BETWEEN begin_time and end_time and used_min_amount <= ?2")
	List<PhVoucherProject> findUseByMerchant(Long merchantId,Double amount);
}
