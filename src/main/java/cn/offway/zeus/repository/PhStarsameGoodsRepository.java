package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhStarsameGoods;
import java.lang.Long;
import java.util.List;

/**
 * 明星同款商品Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhStarsameGoodsRepository extends JpaRepository<PhStarsameGoods,Long>,JpaSpecificationExecutor<PhStarsameGoods> {

	List<PhStarsameGoods> findByStarsameIdOrderByCreateTimeDesc(Long starsameid);
}
