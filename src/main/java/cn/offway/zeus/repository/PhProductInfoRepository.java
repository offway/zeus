package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhProductInfo;
import cn.offway.zeus.dto.ProductJoin;

/**
 * 活动产品表Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhProductInfoRepository extends JpaRepository<PhProductInfo,Long>,JpaSpecificationExecutor<PhProductInfo> {

	@Modifying
    @Transactional
	@Query(nativeQuery=true,value="UPDATE ph_product_info SET `status` = (CASE WHEN NOW() < begin_time THEN '02' WHEN NOW() >= end_time THEN '03' ELSE '01' END )")
	int updateStatus();
	
	
	@Query(nativeQuery=true,value="select * from ph_product_info where NOW() >= begin_time and NOW() < end_time order by begin_time desc")
	List<PhProductInfo> findByNow();
	
	@Query(nativeQuery=true,value="select * from ph_product_info where NOW() < begin_time order by begin_time asc")
	List<PhProductInfo> findBynext();
	
	@Query(nativeQuery=true,value="select * from ph_product_info where NOW() >= end_time order by end_time desc")
	List<PhProductInfo> findByBefore();
	
	@Query(nativeQuery=true,value="select i.* from ph_product_info i where i.id in (select DISTINCT(t.product_id) from ph_lottery_ticket t where t.source='0' and t.unionid = ?1)")
	List<PhProductInfo> findByUnionid(String unionid);
	
	@Query(value="select new cn.offway.zeus.dto.ProductJoin(p.id,p.image,p.name,t.createTime,p.beginTime,p.endTime) from PhLotteryTicket t,PhProductInfo p where p.id=t.productId and t.unionid=?1 and source='0'")
	List<ProductJoin> findProductJoinByUnionid(String unionid);
}
