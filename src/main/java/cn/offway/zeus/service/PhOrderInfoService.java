package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhOrderInfo;

/**
 * 订单Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhOrderInfoService{

	PhOrderInfo save(PhOrderInfo phOrderInfo);
	
	PhOrderInfo findOne(Long id);
}
