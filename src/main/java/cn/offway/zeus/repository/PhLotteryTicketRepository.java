package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhLotteryTicket;

/**
 * 抽奖券表Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhLotteryTicketRepository extends JpaRepository<PhLotteryTicket,Long>,JpaSpecificationExecutor<PhLotteryTicket> {

	@Modifying
    @Transactional
	@Query(nativeQuery=true,value="UPDATE ph_lottery_ticket set CODE=CONCAT('OW',LPAD(nextval(product_id), 6, 0)) where CODE is null")
	int updateCode();
	
	int countByProductIdAndUnionidAndSource(Long productId,String unionid,String source);
	
	List<PhLotteryTicket> findByProductIdAndUnionid(Long productId,String unionid);
	
	@Modifying
    @Transactional
	@Query(nativeQuery=true,value="UPDATE ph_lottery_ticket set form_id=?3 where product_id = ?1 and unionid =?2 and source=0")
	int updateFormId(Long productId,String unionid,String formId);
	
	@Query(nativeQuery=true,value="select distinct(t.product_id) from ph_lottery_ticket t,ph_product_info p where p.id=t.product_id and t.unionid =?1 and now()>=p.begin_time and now()<p.end_time")
	List<Object> findProductId(String unionid);
	
	int countBySource(String source);
	
	int countByUnionidAndSource(String unionid,String source);
}
