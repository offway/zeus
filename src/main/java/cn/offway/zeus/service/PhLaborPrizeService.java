package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhLaborPrize;

/**
 * 劳动节活动奖品Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhLaborPrizeService{

	PhLaborPrize save(PhLaborPrize phLaborPrize);
	
	PhLaborPrize findById(Long id);
}
