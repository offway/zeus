package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhCapitalFlow;

/**
 * 资金流水Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhCapitalFlowRepository extends JpaRepository<PhCapitalFlow,Long>,JpaSpecificationExecutor<PhCapitalFlow> {

	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="insert into ph_capital_flow select null,i.user_id,'0',o.order_no,'0',ROUND(o.amount*0.03,2),NOW(),null from ph_order_info o,ph_invite_info i where i.invite_user_id=o.user_id and o.`status`='3' and DATE_FORMAT(NOW(),'%Y-%m-%d') = DATE_FORMAT(DATE_ADD(o.receipt_time,INTERVAL 8 DAY),'%Y-%m-%d')")
	int insertByReturnAmount();
	
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_user_info u set u.version = u.version+1,u.balance = u.balance +(select IFNULL(SUM(ROUND(o.amount*0.03,2)),0) from ph_order_info o,ph_invite_info i where i.user_id=u.id and i.invite_user_id=o.user_id and o.`status`='3' and DATE_FORMAT(NOW(),'%Y-%m-%d') = DATE_FORMAT(DATE_ADD(o.receipt_time,INTERVAL 8 DAY),'%Y-%m-%d')) where id in(select DISTINCT(i.user_id) from ph_order_info o,ph_invite_info i where i.invite_user_id=o.user_id and o.`status`='3' and DATE_FORMAT(NOW(),'%Y-%m-%d') = DATE_FORMAT(DATE_ADD(o.receipt_time,INTERVAL 8 DAY),'%Y-%m-%d'))")
	int updateBalanceByReturnAmount();
}
