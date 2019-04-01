package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhOrderExpressInfo;

/**
 * 订单物流Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhOrderExpressInfoService{

	PhOrderExpressInfo save(PhOrderExpressInfo phOrderExpressInfo);
	
	PhOrderExpressInfo findOne(Long id);
}
