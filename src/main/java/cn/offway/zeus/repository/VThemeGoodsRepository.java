package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.VThemeGoods;

/**
 * VIEWRepository接口
 *
 * @author tbw
 * @version $v: 1.0.0, $time:2020-03-04 14:10:13 Exp $
 */
public interface VThemeGoodsRepository extends JpaRepository<VThemeGoods,Long>,JpaSpecificationExecutor<VThemeGoods> {

	/** 此处写一些自定义的方法 **/
}
