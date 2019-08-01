package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhRefundOrderGoods;

import java.util.List;

/**
 * 退换货后的订单商品Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-08-01 14:42:53 Exp $
 */
public interface PhRefundOrderGoodsRepository extends JpaRepository<PhRefundOrderGoods,Long>,JpaSpecificationExecutor<PhRefundOrderGoods> {

    List<PhRefundOrderGoods> findByOrderNo(String orderNo);

}
