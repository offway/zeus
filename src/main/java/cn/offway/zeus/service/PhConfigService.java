package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhConfig;

/**
 * 配置Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhConfigService{

	PhConfig save(PhConfig phConfig);
	
	PhConfig findById(Long id);

	String findContentByName(String name);

	PhConfig findByName(String name);

	List<PhConfig> findByNameIn(String... names);
}
