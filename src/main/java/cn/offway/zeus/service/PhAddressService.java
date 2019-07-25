package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhAddress;


/**
 * 地址管理Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-02-12 11:26:00 Exp $
 */
public interface PhAddressService{

	PhAddress save(PhAddress phAddress);
	
	PhAddress findById(Long id);

	void deleteById(Long id);

	List<PhAddress> findByUserId(Long userId);

	PhAddress findDefault(Long userId);
}
