package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhWinningRecord;

/**
 * 中奖用户信息Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhWinningRecordService{

	PhWinningRecord save(PhWinningRecord phWinningRecord);
	
	PhWinningRecord findById(Long id);

	List<PhWinningRecord> findByProductId(Long productId);
}
