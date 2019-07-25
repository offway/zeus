package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhPromotionInfo;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 促销活动Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhPromotionInfoRepository extends JpaRepository<PhPromotionInfo,Long>,JpaSpecificationExecutor<PhPromotionInfo> {

    @Query(nativeQuery = true,value = "select * from ph_promotion_info ppi where ppi.`status`='1' and ppi.`type`='1' and ppi.`merchant_id`=?1 and ppi.begin_time<=NOW() and ppi.end_time >NOW() and EXISTS(select 1 from ph_promotion_goods ppg where ppi.id = ppg.promotion_id and ppg.goods_id in (?2))")
	List<PhPromotionInfo> findByMerchantIdAndGoodsId(Long merchantId,List<Long> goodsIds);

    @Query(nativeQuery = true,value = "select * from ph_promotion_info ppi where ppi.`status`='1' and ppi.`type`='0' and ppi.begin_time<=NOW() and ppi.end_time >NOW() and EXISTS(select 1 from ph_promotion_goods ppg where ppi.id = ppg.promotion_id and ppg.goods_id in (?1))")
    List<PhPromotionInfo> findByPlatformAndGoodsId(List<Long> goodsIds);

    @Query(nativeQuery = true,value = "select ppi.name from ph_promotion_info ppi where ppi.`status`='1' and ppi.begin_time<=NOW() and ppi.end_time >NOW() and EXISTS(select 1 from ph_promotion_goods ppg where ppi.id = ppg.promotion_id and ppg.goods_id = ?1) order by ppi.id limit 1")
    String findNameByGoodsId(Long goodsId);
}
