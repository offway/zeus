package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.VThemeGoods;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * VIEWRepository接口
 *
 * @author tbw
 * @version $v: 1.0.0, $time:2020-03-04 14:10:13 Exp $
 */
public interface VThemeGoodsRepository extends JpaRepository<VThemeGoods,Long>,JpaSpecificationExecutor<VThemeGoods> {

	@Query(nativeQuery = true,value = "select * from v_theme_goods where theme_id = ?1 ORDER BY theme_goods_id LIMIT 0,10 ")
    List<VThemeGoods> findAllTop10(Long id);
}
