package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhPromotionRule;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 促销活动规则Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhPromotionRuleRepository extends JpaRepository<PhPromotionRule,Long>,JpaSpecificationExecutor<PhPromotionRule> {

    @Query(nativeQuery = true,value = "select * from ph_promotion_rule ppr where ppr.promotion_id=?1 and ppr.reduce_limit <=?2 order by ppr.reduce_amount desc limit 1")
    PhPromotionRule findByPromotionIdAnAndReduceLimit(Long promotionId,Double goodsAmount);

    @Query(nativeQuery = true,value = "select * from ph_promotion_rule ppr where ppr.promotion_id=?1 and ppr.gift_limit <=?2 order by ppr.gift_limit desc limit 1")
    PhPromotionRule findByPromotionIdAndGiftLimit(Long promotionId,Double goodsAmount);

    @Query(nativeQuery = true,value = "select * from ph_promotion_rule ppr where ppr.promotion_id=?1 and ppr.discount_num <=?2 order by ppr.discount_rate asc limit 1")
    PhPromotionRule findByPromotionIdAndDiscountNum(Long promotionId,int discountNum);

    @Query(nativeQuery = true,value = "select * from ph_promotion_rule ppr where ppr.promotion_id=?1 and ppr.discount_num >=?2 order by ppr.discount_rate asc limit 1")
    PhPromotionRule qucoudan(Long promotionId,int discountNum);

    @Query(nativeQuery = true,value = "select * from ph_promotion_rule ppr where ppr.promotion_id=?1 and ppr.reduce_limit >=?2 order by ppr.reduce_amount desc limit 1")
    PhPromotionRule qucoudanReduce(Long promotionId,double amount);

    @Query(nativeQuery = true,value = "select * from ph_promotion_rule ppr where ppr.promotion_id=?1 and ppr.gift_limit >=?2 order by ppr.gift_limit desc limit 1")
    PhPromotionRule qucoudanGift(Long promotionId,double amount);

    @Query(nativeQuery = true,value = "select * from ph_promotion_rule ppr where ppr.promotion_id=?1 order by ppr.discount_num asc")
    List<PhPromotionRule> findByPromotionId(Long promotionId);

    @Query(nativeQuery = true,value = "SELECT\n" +
            "\tppr.*\n" +
            "FROM\n" +
            "\tph_promotion_rule ppr\n" +
            "WHERE\n" +
            "\tppr.promotion_id IN (\n" +
            "SELECT\n" +
            "\t\t\tppi.id\n" +
            "\t\tFROM\n" +
            "\t\t\tph_promotion_info ppi\n" +
            "\t\tWHERE\n" +
            "\t\t\tppi.`status` = '1'\n" +
            "\t\tAND ppi.`type` = '0'\n" +
            "\t\tAND ppi.begin_time <= NOW()\n" +
            "\t\tAND ppi.end_time > NOW()\n" +
            "\t\tAND EXISTS (\n" +
            "\t\t\tSELECT\n" +
            "\t\t\t\t1\n" +
            "\t\t\tFROM\n" +
            "\t\t\t\tph_promotion_goods ppg\n" +
            "\t\t\tWHERE\n" +
            "\t\t\t\tppi.id = ppg.promotion_id\n" +
            "\t\t\tAND ppg.goods_id IN (?1)\n" +
            "\t\t)\n" +
            "\t)")
    List<PhPromotionRule> findByPlatform(List<Long> goodsIds);
}
