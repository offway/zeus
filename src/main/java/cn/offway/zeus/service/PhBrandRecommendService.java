package cn.offway.zeus.service;


import java.util.List;

import cn.offway.zeus.domain.PhBrandRecommend;

/**
 * 品牌推荐表Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2020-03-02 12:24:25 Exp $
 */
public interface PhBrandRecommendService{

    PhBrandRecommend save(PhBrandRecommend phBrandRecommend);
	
    PhBrandRecommend findOne(Long id);

    void delete(Long id);

    List<PhBrandRecommend> save(List<PhBrandRecommend> entities);

    List<PhBrandRecommend> findAllRecommend();
}
