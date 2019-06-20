package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhMerchantBrand;
import java.lang.Long;
import java.util.List;

/**
 * 商户品牌关系Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhMerchantBrandRepository extends JpaRepository<PhMerchantBrand,Long>,JpaSpecificationExecutor<PhMerchantBrand> {

	List<PhMerchantBrand> findByMerchantId(Long merchantId);
}
