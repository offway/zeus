package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhGoodsProperty;
import java.lang.Long;
import java.util.List;


/**
 * 商品属性Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhGoodsPropertyRepository extends JpaRepository<PhGoodsProperty,Long>,JpaSpecificationExecutor<PhGoodsProperty> {

	List<PhGoodsProperty> findByGoodsIdOrderBySortAsc(Long goodsId);
	
	List<PhGoodsProperty> findByGoodsStockIdOrderBySortAsc(Long goodsStockId);
}
