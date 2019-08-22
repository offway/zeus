package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhOrderGoods;
import org.springframework.data.jpa.repository.Query;

import java.lang.String;
import java.util.Date;
import java.util.List;

/**
 * 订单商品Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhOrderGoodsRepository extends JpaRepository<PhOrderGoods,Long>,JpaSpecificationExecutor<PhOrderGoods> {

	List<PhOrderGoods> findByPreorderNo(String preorderno);
	
	List<PhOrderGoods> findByOrderNo(String orderNo);

	@Query(nativeQuery = true,value = "select SUM(goods_count) from ph_order_goods where order_no=?1")
	int sumGoodsCountByOrderNo(String orderNo);

	@Query(nativeQuery = true,value = "select ifnull(SUM(og.goods_count),0) from ph_order_goods og where og.goods_id =?1 and EXISTS(select 1 from ph_order_info o where  o.order_no = og.order_no and o.user_id = ?2 and  o.status !='4'  and o.create_time BETWEEN ?3 and ?4)")
	int sumGoodsCountByLimitSale(Long goodsId, Long userId, Date beginTime, Date endTime);
}
