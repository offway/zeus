package cn.offway.zeus.service;

import java.util.List;

import org.springframework.data.domain.Sort;

import cn.offway.zeus.domain.PhLaborLucky;

/**
 * 劳动节活动中奖记录Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhLaborLuckyService{

	PhLaborLucky save(PhLaborLucky phLaborLucky);
	
	PhLaborLucky findById(Long id);

	List<PhLaborLucky> findByUserIdOrderByCreateTimeDesc(Long userId);

	List<PhLaborLucky> findByOrderByCreateTimeDesc();
}
