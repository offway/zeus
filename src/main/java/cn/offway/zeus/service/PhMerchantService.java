package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhMerchant;

/**
 * 商户表Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhMerchantService{

	PhMerchant save(PhMerchant phMerchant);
	
	PhMerchant findOne(Long id);

	double calculateFare(Long id, int num, Long addrId);
}
