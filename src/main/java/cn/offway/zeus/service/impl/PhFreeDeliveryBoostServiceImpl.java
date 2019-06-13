package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhFreeDeliveryBoostService;

import cn.offway.zeus.domain.PhFreeDeliveryBoost;
import cn.offway.zeus.repository.PhFreeDeliveryBoostRepository;


/**
 * 免费送助力Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhFreeDeliveryBoostServiceImpl implements PhFreeDeliveryBoostService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhFreeDeliveryBoostRepository phFreeDeliveryBoostRepository;
	
	@Override
	public PhFreeDeliveryBoost save(PhFreeDeliveryBoost phFreeDeliveryBoost){
		return phFreeDeliveryBoostRepository.save(phFreeDeliveryBoost);
	}
	
	@Override
	public PhFreeDeliveryBoost findOne(Long id){
		return phFreeDeliveryBoostRepository.findOne(id);
	}
}
