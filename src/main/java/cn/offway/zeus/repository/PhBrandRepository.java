package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhBrand;

/**
 * 品牌库Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhBrandRepository extends JpaRepository<PhBrand,Long>,JpaSpecificationExecutor<PhBrand> {

	List<PhBrand> findByIsRecommendAndStatusOrderBySortAsc(String isRecommend,String status);
	
	List<PhBrand> findByTypeAndStatusOrderByNameAsc(String type,String status);
	
	@Query(nativeQuery=true,value="select * from ph_brand where status='1' and name =?1 limit 1")
	PhBrand findByName(String name);
	
	@Query(nativeQuery=true,value="select * from ph_brand where status='1' and id in (select brand_id from ph_merchant_brand where merchant_id=?1 )")
	List<PhBrand> findByMerchantId(Long merchantId);
	
	@Query(nativeQuery=true,value="select * from ph_brand where status='1' and name like ?1")
	List<PhBrand> findByNameLike(String name);
	
	@Query(nativeQuery=true,value="select name from ph_brand where status='1' and name like ?1")
	List<String> findNameLike(String name);

	@Query(nativeQuery=true,value="select * from ph_brand where status = '1' and id in(select DISTINCT brand_id from ph_goods where `status` = '1' ORDER BY brand_id desc) ORDER BY id desc LIMIT 0,10")
	List<PhBrand> findNewTop10();
}
