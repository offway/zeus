package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhPick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhChannelUserService;

import cn.offway.zeus.domain.PhChannelUser;
import cn.offway.zeus.repository.PhChannelUserRepository;


/**
 * 用户推广渠道表Service接口实现
 *
 * @author tbw
 * @version $v: 1.0.0, $time:2020-01-13 13:36:31 Exp $
 */
@Service
public class PhChannelUserServiceImpl implements PhChannelUserService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhChannelUserRepository phChannelUserRepository;
	
	@Override
	public PhChannelUser save(PhChannelUser phChannelUser){
		return phChannelUserRepository.save(phChannelUser);
	}
	
	@Override
	public PhChannelUser findOne(Long id){
		Optional<PhChannelUser> optional = phChannelUserRepository.findById(id);
		if (optional.isPresent()){
			return optional.get();
		}
		return null;
	}

	@Override
	public void delete(Long id){
		phChannelUserRepository.deleteById(id);
	}

	@Override
	public List<PhChannelUser> save(List<PhChannelUser> entities){
		return phChannelUserRepository.saveAll(entities);
	}

	@Override
	public PhChannelUser findByChannel(String channel){
		return phChannelUserRepository.findByChannel(channel);
	}
}
