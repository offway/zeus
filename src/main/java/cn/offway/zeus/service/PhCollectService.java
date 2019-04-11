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
	
	PhCollect findOne(Long id);

	void collect(Long userId, String type, Long matchId) throws Exception;

	void delete(Long id);
}
