package cn.offway.zeus.repository;

import cn.offway.zeus.domain.PhLimitedSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhBrandRecommend;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 品牌推荐表Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2020-03-02 12:24:25 Exp $
 */
public interface PhBrandRecommendRepository extends JpaRepository<PhBrandRecommend,Long>,JpaSpecificationExecutor<PhBrandRecommend> {

    @Query(nativeQuery=true,value="SELECT * FROM ph_brand_recommend WHERE is_recommend = '1' ORDER BY sort ")
    List<PhBrandRecommend> findAllRecommend();

}
