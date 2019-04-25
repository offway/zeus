package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhOrderInfo;
import java.lang.String;
import java.util.List;

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
	
	List<PhOrderInfo> findByPreorderNoAndStatus(String preorderno,String status);
	
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true,value="update ph_order_info set status=?3,pay_channel=?4,version=version+1 where preorder_no=?1 and status=?2")
	int updateStatusByPreOrderNo(String preorderno,String fromstauts,String tostauts,String payChannel);
	
	PhOrderInfo findByOrderNo(String orderNo);
	
	int countByUserIdAndStatusAndIsHidden(Long userId,String status,String isHidden);
	
	@Query(nativeQuery=true,value="select * from ph_order_info o where o.`status`='3' and DATE_FORMAT(NOW(),'%Y-%m-%d') = DATE_FORMAT(DATE_ADD(o.receipt_time,INTERVAL 8 DAY),'%Y-%m-%d') and exists(select 1 from ph_invite_info i where i.invite_user_id=o.user_id)")
	List<PhOrderInfo> returnAmountOrders();
	
	@Query(nativeQuery=true,value="select phone from ph_address where id in (select id from ph_merchant where id in (select merchant_id from ph_order_info where preorder_no=?1))")
	List<String> findMerchantPhone(String preorderno);
}
