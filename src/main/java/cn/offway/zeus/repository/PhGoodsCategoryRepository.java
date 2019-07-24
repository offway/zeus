package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhGoodsCategory;
import java.lang.Long;
import java.util.List;
import java.lang.String;

/**
 * 商品类目Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhGoodsCategoryRepository extends JpaRepository<PhGoodsCategory,Long>,JpaSpecificationExecutor<PhGoodsCategory> {

	List<PhGoodsCategory> findByGoodsTypeNameOrderBySortAsc(String goodsTypeName);
	
	@Query(nativeQuery=true,value="select distinct(name) from ph_goods_category where name like ?1")
	List<String> findByNameLike(String name);
}
