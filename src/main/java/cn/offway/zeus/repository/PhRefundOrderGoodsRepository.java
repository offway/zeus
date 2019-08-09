package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhRefundOrderGoods;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 退换货后的订单商品Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-08-01 14:42:53 Exp $
 */
public interface PhRefundOrderGoodsRepository extends JpaRepository<PhRefundOrderGoods,Long>,JpaSpecificationExecutor<PhRefundOrderGoods> {

    List<PhRefundOrderGoods> findByOrderNo(String orderNo);

    int countByOrderNo(String orderNo);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value="insert into ph_refund_order_goods select null,preorder_no,order_no,price,goods_id,goods_name,goods_image,brand_id,brand_name,brand_logo,goods_count,create_time,remark,merchant_id,merchant_logo,merchant_name,goods_stock_id,amount from ph_order_goods where order_no=?1")
    int insertOrderGoods(String orderNo);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value="update  ph_refund_order_goods set goods_count = goods_count - ?3 where order_no=?1 and goods_stock_id =?2")
    int subGoodsCount(String orderNo,Long goodsStockId,int count);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value="insert into ph_refund_order_goods select null,preorder_no,order_no,price,goods_id,goods_name,goods_image,brand_id,brand_name,brand_logo,goods_count,create_time,remark,merchant_id,merchant_logo,merchant_name,goods_stock_id,amount from ph_order_goods where id =?1")
    int insertGoodsStock(Long orderGoodsId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value="update ph_refund_order_goods set goods_count = ?3,goods_stock_id =?4,goods_image=?5,remark=?6 where order_no=?1 and goods_stock_id =?2 order by id desc limit 1")
    int updateGoodsStock(String orderNo,Long formGoodsStockId,int count,Long toGoodsStockId,String toStockImage,String remark);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value="DELETE from ph_refund_order_goods where order_no=?1 and goods_count <=0")
    int deleteByNoMore(String orderNo);

}
