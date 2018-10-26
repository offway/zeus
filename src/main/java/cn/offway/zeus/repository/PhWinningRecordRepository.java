package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhWinningRecord;

/**
 * 中奖用户信息Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhWinningRecordRepository extends JpaRepository<PhWinningRecord,Long>,JpaSpecificationExecutor<PhWinningRecord> {

	List<PhWinningRecord> findByProductId(Long productId);
}
