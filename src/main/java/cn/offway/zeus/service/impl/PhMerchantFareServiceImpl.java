package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhMerchantFareService;

import cn.offway.zeus.domain.PhMerchantFare;
import cn.offway.zeus.repository.PhMerchantFareRepository;


/**
 * 商户运费表Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhMerchantFareServiceImpl implements PhMerchantFareService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhMerchantFareRepository phMerchantFareRepository;
	
	@Override
	public PhMerchantFare save(PhMerchantFare phMerchantFare){
		return phMerchantFareRepository.save(phMerchantFare);
	}
	
	@Override
	public PhMerchantFare findOne(Long id){
		return phMerchantFareRepository.findOne(id);
	}
	
}
