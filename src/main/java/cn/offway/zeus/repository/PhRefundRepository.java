package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhRefund;

/**
 * 退款/退货Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhRefundRepository extends JpaRepository<PhRefund,Long>,JpaSpecificationExecutor<PhRefund> {

	//	@Query(nativeQuery=true,value="select count(*) from ph_refund r where  r.`status` in ('0','1','3','4') and r.is_complete='1' and r.order_no=?1")
	@Query(nativeQuery=true,value="select count(*) from ph_refund r where  r.`status` in ('0','1','3','4') and r.order_no=?1")
	int isCompleteOrderNo(String orderNo);
}
