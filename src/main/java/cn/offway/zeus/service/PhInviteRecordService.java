package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhInviteRecord;

/**
 * 邀请记录表Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhInviteRecordService{

	PhInviteRecord save(PhInviteRecord phInviteRecord);
	
	PhInviteRecord findOne(Long id);

	List<PhInviteRecord> findByProductIdAndUnionid(Long productId, String unionid);

	int countByProductIdAndUnionid(Long productId, String unionid);
}
