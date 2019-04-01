package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhGoodsImage;

/**
 * 商品图片Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhGoodsImageRepository extends JpaRepository<PhGoodsImage,Long>,JpaSpecificationExecutor<PhGoodsImage> {

	/** 此处写一些自定义的方法 **/
}
