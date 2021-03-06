package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhGoodsSpecial;

/**
 * 特殊商品Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhGoodsSpecialRepository extends JpaRepository<PhGoodsSpecial,Long>,JpaSpecificationExecutor<PhGoodsSpecial> {

	int countByGoodsId(Long goodsId);
}
