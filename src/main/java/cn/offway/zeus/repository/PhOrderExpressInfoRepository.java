package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhOrderExpressInfo;

/**
 * 订单物流Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhOrderExpressInfoRepository extends JpaRepository<PhOrderExpressInfo,Long>,JpaSpecificationExecutor<PhOrderExpressInfo> {

	PhOrderExpressInfo findByOrderNoAndType(String orderno,String type);
}
