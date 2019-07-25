package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhMerchantFare;

/**
 * 商户运费表Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhMerchantFareService{

	PhMerchantFare save(PhMerchantFare phMerchantFare);
	
	PhMerchantFare findById(Long id);

}
