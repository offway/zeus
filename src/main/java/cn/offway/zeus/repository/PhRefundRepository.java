package cn.offway.zeus.repository;

import java.util.List;

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

	//
	@Query(nativeQuery=true,value="select count(*) from ph_refund r where r.type !='2' and r.`status` not in ('5','6') and r.order_no=?1")
	int refunded(String orderNo);

	@Query(nativeQuery=true,value="select ifnull(SUM(goods_count),0) from ph_refund where type!='2' and `status` not in ('5','6') and order_no=?1")
	int sumGoodsCountByOrderNo(String orderNo);

	@Query(nativeQuery=true,value="select count(*) from ph_refund r where r.type!='2' and r.`status` not in ('4','5','6') and r.order_no=?1")
	int isRefunding(String orderNo);
	
	int countByUserIdAndStatusIn(Long userId,List<String> status);

	@Query(nativeQuery = true,value = "select count(*) from ph_refund where `status` in ('0','1','2','3')  and order_no =?1 ")
	int refundIng(String orderNo);

	@Query(nativeQuery = true,value = "select * from ph_refund where `status` not in ('5','6')  and order_no =?1 order by id desc limit 1")
	PhRefund findByOrderNoEnd(String orderNo);

	@Query(nativeQuery = true,value = "select count(*) from ph_refund where `status` ='4' and order_no =?1 ")
	int refundSuccess(String orderNo);

}
