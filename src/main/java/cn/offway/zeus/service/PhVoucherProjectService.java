package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhVoucherProject;

/**
 * 优惠券方案Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhVoucherProjectService{

	PhVoucherProject save(PhVoucherProject phVoucherProject);
	
	PhVoucherProject findOne(Long id);
}
