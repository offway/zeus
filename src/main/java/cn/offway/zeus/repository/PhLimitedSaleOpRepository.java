package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhLimitedSaleOp;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 限量发售用户操作Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhLimitedSaleOpRepository extends JpaRepository<PhLimitedSaleOp,Long>,JpaSpecificationExecutor<PhLimitedSaleOp> {

	int countByLimitedSaleIdAndUserIdAndType(Long limitedSaleId,Long userId,String type);

	int countByLimitedSaleIdAndUserIdAndTypeAndBoostUserId(Long limitedSaleId,Long userId,String type,Long boostUserId);

	@Transactional
	@Modifying
	@Query(nativeQuery = true,value="insert into ph_limited_sale_op select null,ls.id,'0',?1,NOW(),null,?3 from ph_limited_sale ls where ls.id in (?2)")
	int boostBatch(Long userId,List<String> limitedSaleIds,Long boostUserId);

}
