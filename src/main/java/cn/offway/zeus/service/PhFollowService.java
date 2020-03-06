package cn.offway.zeus.service;


import java.util.List;

import cn.offway.zeus.domain.PhFollow;

/**
 * 关注列表Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2020-03-02 13:55:02 Exp $
 */
public interface PhFollowService{

    PhFollow save(PhFollow phFollow);
	
    PhFollow findOne(Long id);

    void delete(Long id);

    List<PhFollow> save(List<PhFollow> entities);

    void deleteByUidAndCelebrityid(String uid, Long id);

    List<PhFollow> findByUnionid(String uid);
}
