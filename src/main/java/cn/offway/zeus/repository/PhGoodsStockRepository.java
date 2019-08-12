package cn.offway.zeus.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhGoodsStock;

/**
 * 商品库存Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhGoodsStockRepository extends JpaRepository<PhGoodsStock,Long>,JpaSpecificationExecutor<PhGoodsStock> {

	List<PhGoodsStock> findByGoodsId(Long goodsId);
	
	List<PhGoodsStock> findByIdIn(Set<Long> ids);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_goods_stock s set s.stock =  s.stock-?2 , version = version+1 where  s.id=?1  and s.stock>=?2")
	int updateStock(Long stockId,Long count);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_goods_stock s set s.stock =  s.stock+?2 , version = version+1 where  s.id=?1 ")
	int addStock(Long stockId,Long count);
	
	@Query(nativeQuery=true,value="select ifnull(sum(s.stock),0) from  ph_goods_stock s  where  s.goods_id=?1 ")
	int sumStock(Long goodsId);

	int countByIdAndStockIsLessThan(Long id,Long num);
}
