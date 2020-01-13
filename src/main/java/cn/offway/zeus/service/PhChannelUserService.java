package cn.offway.zeus.service;


import java.util.List;

import cn.offway.zeus.domain.PhChannelUser;

/**
 * 用户推广渠道表Service接口
 *
 * @author tbw
 * @version $v: 1.0.0, $time:2020-01-13 13:36:31 Exp $
 */
public interface PhChannelUserService{

    PhChannelUser save(PhChannelUser phChannelUser);
	
    PhChannelUser findOne(Long id);

    void delete(Long id);

    List<PhChannelUser> save(List<PhChannelUser> entities);

    PhChannelUser findByChannel(String channel);
}
