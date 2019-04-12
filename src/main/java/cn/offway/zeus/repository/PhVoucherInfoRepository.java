package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhVoucherInfo;

/**
 * 优惠券Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhVoucherInfoRepository extends JpaRepository<PhVoucherInfo,Long>,JpaSpecificationExecutor<PhVoucherInfo> {

	@Query(nativeQuery=true,value="select * from ph_voucher_info where user_id =?1  and is_lock='0' and used_min_amount<=?4 and (used_type='0' or (used_type='1' and `condition`='0' and LOCATE(CONCAT(',',?2,','),match_ids)>0 ) or (used_type='1' and `condition`='1' and LOCATE(CONCAT(',',?2,','),match_ids)=0 ) or (used_type='2' and `condition`='0' and LOCATE(CONCAT(',',?3,','),match_ids)>0 ) or (used_type='2' and `condition`='1' and LOCATE(CONCAT(',',?3,','),match_ids)=0 )) order by create_time desc")
	List<PhVoucherInfo> list(Long userId,Long goodsId,Long brandId,Double amount);
	
	List<PhVoucherInfo> findByUserIdOrderByCreateTimeDesc(Long userId);
}
