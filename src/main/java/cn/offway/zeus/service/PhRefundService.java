package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhRefund;

/**
 * 退款/退货Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhRefundService{

	PhRefund save(PhRefund phRefund);
	
	PhRefund findOne(Long id);
}
