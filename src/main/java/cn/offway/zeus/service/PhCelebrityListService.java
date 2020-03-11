package cn.offway.zeus.service;


import java.util.List;

import cn.offway.zeus.domain.PhCelebrityList;

/**
 * 明星信息表Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2020-03-02 13:55:02 Exp $
 */
public interface PhCelebrityListService{

    PhCelebrityList save(PhCelebrityList phCelebrityList);
	
    PhCelebrityList findOne(Long id);

    void delete(Long id);

    List<PhCelebrityList> save(List<PhCelebrityList> entities);

    List<PhCelebrityList> findBynNameLike(String name);

    List<PhCelebrityList> finAll();

    List<PhCelebrityList> findByName(String name);

    List<PhCelebrityList> findPage(Long page, Long size);
}
