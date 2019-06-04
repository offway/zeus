package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhLimitedSale;
import java.lang.Long;
import java.util.List;

/**
 * 限量发售Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhLimitedSaleRepository extends JpaRepository<PhLimitedSale,Long>,JpaSpecificationExecutor<PhLimitedSale> {

	@Query(nativeQuery=true,value="select * from ph_limited_sale where goods_id =?1 limit 1")
	PhLimitedSale findByGoodsId(Long goodsId);
}
