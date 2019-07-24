package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhRefundGoods;
import java.lang.Long;
import java.util.List;

/**
 * 退款/退货商品明细Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhRefundGoodsRepository extends JpaRepository<PhRefundGoods,Long>,JpaSpecificationExecutor<PhRefundGoods> {

	@Query(nativeQuery=true,value="select IFNULL(SUM(rg.goods_count),0) from ph_refund_goods rg,ph_refund r where rg.refund_id = r.id and r.`status` in ('0','1','3','4') and rg.order_goods_id=?1")
	Long refundGoodsCount(Long orderGoodsId);
	
	int deleteByRefundId(Long refundId);
	
	List<PhRefundGoods> findByRefundId(Long refundId);
}
