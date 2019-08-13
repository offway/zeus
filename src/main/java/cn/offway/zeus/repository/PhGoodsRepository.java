package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhGoods;
import java.lang.Long;

/**
 * 商品表Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhGoodsRepository extends JpaRepository<PhGoods,Long>,JpaSpecificationExecutor<PhGoods> {

	@Query(nativeQuery=true,value="select g.* from ph_goods g ,ph_brand b where g.brand_id = b.id and b.type='0' and g.`status`='1'and b.`status`='1' order by g.sale_count desc,g.up_time desc limit 10")
	List<PhGoods> indexData();
	
	@Query(nativeQuery=true,value="select g.* from ph_goods g ,ph_brand b where g.brand_id = b.id and b.id=?1 and g.`status`='1' and b.`status`='1' order by g.sale_count desc,g.up_time desc limit 10")
	List<PhGoods> findBrandRecommend(Long brandId);
	
	@Query(nativeQuery=true,value="select* from ph_goods g where g.`status`='1' and g.id not in (4465,?1) and EXISTS(select 1 from ph_goods pg where pg.type = g.type and pg.category=g.category  and pg.brand_id= g.brand_id  and pg.id=?1) ORDER BY g.create_time desc limit 3")
	List<PhGoods> findRecommend(Long id);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_goods set view_count = view_count+1 where id=?1")
	int updateViewCount(Long id);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_goods g set g.sale_count = g.sale_count + ?2 where id=?1")
	int updateSaleCount(Long goodsId,Long count);
	
	@Query(nativeQuery=true,value="select * from ph_goods g where g.`status`='1' and g.id in(?1) ")
	List<PhGoods> findByIds(List<String> ids);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_goods set sort = RAND() where status='1'")
	int updateSort();
	
	@Query(nativeQuery=true,value="select CONCAT(brand_name,' ',category) from ph_goods where `brand_name` =?1 group by category")
	List<String> searchCategory(String brandName);

	int countByIdInAndStatus(List<Long> ids,String status);

	
	
}
