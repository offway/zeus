package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhPreorderMerchantService;

import cn.offway.zeus.domain.PhPreorderMerchant;
import cn.offway.zeus.repository.PhPreorderMerchantRepository;


/**
 * 预生成订单店铺Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhPreorderMerchantServiceImpl implements PhPreorderMerchantService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhPreorderMerchantRepository phPreorderMerchantRepository;
	
	@Override
	public PhPreorderMerchant save(PhPreorderMerchant phPreorderMerchant){
		return phPreorderMerchantRepository.save(phPreorderMerchant);
	}
	
	@Override
	public PhPreorderMerchant findOne(Long id){
		return phPreorderMerchantRepository.findOne(id);
	}
}
