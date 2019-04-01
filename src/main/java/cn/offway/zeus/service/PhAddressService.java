package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhAddress;

/**
 * 地址管理Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhAddressService{

	PhAddress save(PhAddress phAddress);
	
	PhAddress findOne(Long id);
}
