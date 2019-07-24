package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhLimitedSaleOp;

/**
 * 限量发售用户操作Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhLimitedSaleOpRepository extends JpaRepository<PhLimitedSaleOp,Long>,JpaSpecificationExecutor<PhLimitedSaleOp> {

	int countByLimitedSaleIdAndUserIdAndType(Long limitedSaleId,Long userId,String type);
}
