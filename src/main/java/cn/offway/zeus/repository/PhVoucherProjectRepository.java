package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhVoucherProject;
import java.lang.Long;
import java.util.List;

/**
 * 优惠券方案Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhVoucherProjectRepository extends JpaRepository<PhVoucherProject,Long>,JpaSpecificationExecutor<PhVoucherProject> {

	List<PhVoucherProject> findByIdIn(List<Long> ids);
}
