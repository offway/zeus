package cn.offway.zeus.service.impl;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhUserChannelService;

import cn.offway.zeus.domain.PhUserChannel;
import cn.offway.zeus.repository.PhUserChannelRepository;

import java.util.Optional;


/**
 * 用户渠道Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhUserChannelServiceImpl implements PhUserChannelService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhUserChannelRepository phUserChannelRepository;
	
	@Override
	public PhUserChannel save(PhUserChannel phUserChannel){
		return phUserChannelRepository.save(phUserChannel);
	}
	
	@Override
	public PhUserChannel findById(Long id){
		Optional<PhUserChannel> optional = phUserChannelRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public PhUserChannel findByPhone(String phone){
		return phUserChannelRepository.findByPhone(phone);
	}
}
