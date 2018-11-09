package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhProductRule;

/**
 * 活动规则表Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhProductRuleRepository extends JpaRepository<PhProductRule,Long>,JpaSpecificationExecutor<PhProductRule> {

	List<PhProductRule> findByProductId(Long productId);
	
	@Query(nativeQuery=true,value="select content from ph_product_rule where product_id = ?1 order by id")
	List<String> findContontByProductId(Long productId);
}
