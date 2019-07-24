package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhCollect;

/**
 * 收藏夹Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhCollectService{

	PhCollect save(PhCollect phCollect);
	
	PhCollect getOne(Long id);

	Long collect(Long userId, String type, Long matchId) throws Exception;

	void deleteById(Long id);

	Long isCollect(Long userId, String type, Long matchId);
}
