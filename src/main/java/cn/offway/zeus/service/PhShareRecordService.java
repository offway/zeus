package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhShareRecord;

/**
 * 分享记录表Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhShareRecordService{

	PhShareRecord save(PhShareRecord phShareRecord);
	
	PhShareRecord getOne(Long id);

	int countByUnionidAndProductId(String unionid, Long productId);

	PhShareRecord saveShare(Long productId, String unionid, String channel) throws Exception;
}
