package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhInviteRecord;

/**
 * 邀请记录表Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhInviteRecordRepository extends JpaRepository<PhInviteRecord,Long>,JpaSpecificationExecutor<PhInviteRecord> {

	List<PhInviteRecord> findByProductIdAndUnionid(Long productId, String unionid);
	
	int countByProductIdAndUnionid(Long productId, String unionid);
}
