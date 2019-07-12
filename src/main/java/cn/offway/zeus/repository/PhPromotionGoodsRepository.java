package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhPromotionGoods;

/**
 * 促销活动对应商品Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhPromotionGoodsRepository extends JpaRepository<PhPromotionGoods,Long>,JpaSpecificationExecutor<PhPromotionGoods> {

	/** 此处写一些自定义的方法 **/
}
