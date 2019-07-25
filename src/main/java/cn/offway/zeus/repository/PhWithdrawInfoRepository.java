package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhWithdrawInfo;

/**
 * 提现订单Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-07-25 13:18:47 Exp $
 */
public interface PhWithdrawInfoRepository extends JpaRepository<PhWithdrawInfo,Long>,JpaSpecificationExecutor<PhWithdrawInfo> {

	/** 此处写一些自定义的方法 **/
}
