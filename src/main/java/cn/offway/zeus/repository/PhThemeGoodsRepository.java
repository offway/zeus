package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhThemeGoods;

import java.util.List;

/**
 * 主题商品表Repository接口
 *
 * @author tbw
 * @version $v: 1.0.0, $time:2020-03-03 13:55:07 Exp $
 */
public interface PhThemeGoodsRepository extends JpaRepository<PhThemeGoods,Long>,JpaSpecificationExecutor<PhThemeGoods> {

	List<PhThemeGoods> findByThemeId(Long id);
}
