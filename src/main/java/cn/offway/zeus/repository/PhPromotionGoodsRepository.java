package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhPromotionGoods;
import org.springframework.data.jpa.repository.Query;

/**
 * 促销活动对应商品Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhPromotionGoodsRepository extends JpaRepository<PhPromotionGoods,Long>,JpaSpecificationExecutor<PhPromotionGoods> {

    @Query(nativeQuery = true,value = "select ppg.promotion_id from ph_promotion_goods ppg,ph_promotion_info ppi where ppi.id = ppg.promotion_id and ppi.status='1'and ppi.begin_time<=NOW() and ppi.end_time >NOW() and ppg.goods_id = ?1")
	Long findPromotionIdByGoodsId(Long goodsId);

    int countByPromotionIdAndGoodsId(Long promotionId,Long goodsId);

}
