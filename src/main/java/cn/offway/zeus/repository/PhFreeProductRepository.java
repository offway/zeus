package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhFreeProduct;

/**
 * 免费送产品表Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-08-21 13:14:51 Exp $
 */
public interface PhFreeProductRepository extends JpaRepository<PhFreeProduct,Long>,JpaSpecificationExecutor<PhFreeProduct> {

	/** 此处写一些自定义的方法 **/
}
