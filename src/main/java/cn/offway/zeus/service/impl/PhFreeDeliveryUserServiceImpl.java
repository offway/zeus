package cn.offway.zeus.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhFreeDeliveryUserService;

import cn.offway.zeus.domain.PhFreeDeliveryUser;
import cn.offway.zeus.repository.PhFreeDeliveryUserRepository;


/**
 * 免费送参与用户Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhFreeDeliveryUserServiceImpl implements PhFreeDeliveryUserService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhFreeDeliveryUserRepository phFreeDeliveryUserRepository;
	
	@Override
	public PhFreeDeliveryUser save(PhFreeDeliveryUser phFreeDeliveryUser){
		return phFreeDeliveryUserRepository.save(phFreeDeliveryUser);
	}
	
	@Override
	public PhFreeDeliveryUser findOne(Long id){
		return phFreeDeliveryUserRepository.findOne(id);
	}
	
	@Override
	public List<PhFreeDeliveryUser> findByFreeDeliveryId(Long freedeliveryId){
		return phFreeDeliveryUserRepository.findByFreeDeliveryId(freedeliveryId);
	}
	
	@Override
	public PhFreeDeliveryUser findByFreeDeliveryIdAndUserId(Long freedeliveryId,Long userId){
		return phFreeDeliveryUserRepository.findByFreeDeliveryIdAndUserId(freedeliveryId, userId);
	}

	@Override
	public List<PhFreeDeliveryUser> ranking(Long freedeliveryId) {
		return phFreeDeliveryUserRepository.ranking(freedeliveryId);
	}

	@Override
	public int countByFreeDeliveryIdAndCurrentCount(Long freedeliveryId, Long currentCount) {
		return phFreeDeliveryUserRepository.countByFreeDeliveryIdAndCurrentCount(freedeliveryId,currentCount);
	}
}
