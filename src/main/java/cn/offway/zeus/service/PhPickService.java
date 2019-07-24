package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhPick;

/**
 * OFFWAY优选Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhPickService{

	PhPick save(PhPick phPick);
	
	PhPick getOne(Long id);
}
