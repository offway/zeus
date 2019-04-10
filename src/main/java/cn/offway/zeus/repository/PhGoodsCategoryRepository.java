package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhGoodsCategory;
import java.lang.Long;
import java.util.List;

/**
 * 商品类目Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhGoodsCategoryRepository extends JpaRepository<PhGoodsCategory,Long>,JpaSpecificationExecutor<PhGoodsCategory> {

	List<PhGoodsCategory> findByGoodsTypeNameOrderBySortAsc(String goodsTypeName);
}
