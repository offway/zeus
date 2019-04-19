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

	@Query(nativeQuery=true,value="select g.* from ph_goods g ,ph_brand b where g.brand_id = b.id and b.type='0' order by g.sale_count desc limit 10")
	List<PhGoods> indexData();
	
	@Query(nativeQuery=true,value="select* from ph_goods g where g.`status`='1' and g.id!=?1 and EXISTS(select 1 from ph_goods pg where pg.type = g.type and pg.category=g.category  and pg.brand_id= g.brand_id  and pg.id=?1) ORDER BY g.create_time desc limit 3")
	List<PhGoods> findRecommend(Long id);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_goods set view_count = view_count+1 where id=?1")
	int updateViewCount(Long id);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_goods g set g.sale_count = g.sale_count +(select og.goods_count from ph_order_goods og where og.preorder_no=?1 and og.goods_id=g.id) where g.id in (select goods_id from ph_order_goods where preorder_no=?1)")
	int updateSaleCount(String preorderNo);
	
	
}
