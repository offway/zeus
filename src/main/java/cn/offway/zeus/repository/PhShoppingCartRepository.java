package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhShoppingCart;
import java.lang.Long;
import java.util.List;

/**
 * 购物车Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhShoppingCartRepository extends JpaRepository<PhShoppingCart,Long>,JpaSpecificationExecutor<PhShoppingCart> {

	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="update ph_shopping_cart set goods_count=goods_count+?3,price=price+(price*?3) where user_id=?1 and goods_stock_id=?2")
	int updateShoppingCar(Long userId,Long goodsStockId,Long goodsCount);

	PhShoppingCart findByUserIdAndGoodsStockId(Long userId,Long goodsStockId);
	
	int countByUserId(Long userId);
	
	List<PhShoppingCart> findByUserIdOrderByCreateTimeDesc(Long userId);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="delete from ph_shopping_cart  where id in(?1)")
	int delete(List<Long> ids);
}
