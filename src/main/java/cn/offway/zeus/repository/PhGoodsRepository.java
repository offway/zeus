package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhGoods;

/**
 * 商品表Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhGoodsRepository extends JpaRepository<PhGoods,Long>,JpaSpecificationExecutor<PhGoods> {

	@Query(nativeQuery=true,value="select g.* from ph_goods g ,ph_brand b where g.brand_id = b.id and b.type='0' order by g.sale_count desc limit 10")
	List<PhGoods> indexData();
}
