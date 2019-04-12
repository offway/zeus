package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhMerchantBrand;

/**
 * 商户品牌关系Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhMerchantBrandService{

	PhMerchantBrand save(PhMerchantBrand phMerchantBrand);
	
	PhMerchantBrand findOne(Long id);
}
