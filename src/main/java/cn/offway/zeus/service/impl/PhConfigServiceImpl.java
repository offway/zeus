package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhConfigService;

import cn.offway.zeus.domain.PhConfig;
import cn.offway.zeus.repository.PhConfigRepository;


/**
 * 配置Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhConfigServiceImpl implements PhConfigService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhConfigRepository phConfigRepository;
	
	@Override
	public PhConfig save(PhConfig phConfig){
		return phConfigRepository.save(phConfig);
	}
	
	@Override
	public PhConfig findById(Long id){
		Optional<PhConfig> optional = phConfigRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public String findContentByName(String name){
		return phConfigRepository.findContentByName(name);
	}
	
	@Override
	public List<PhConfig> findByNameIn(String... names){
		return phConfigRepository.findByNameIn(names);
	}
}
