package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhPreorderMerchant;

/**
 * 预生成订单店铺Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhPreorderMerchantService{

	PhPreorderMerchant save(PhPreorderMerchant phPreorderMerchant);
	
	PhPreorderMerchant findOne(Long id);
}
