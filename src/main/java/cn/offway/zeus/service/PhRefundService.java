package cn.offway.zeus.service;

import java.util.Map;

import cn.offway.zeus.domain.PhRefund;
import cn.offway.zeus.dto.RefundDto;
import cn.offway.zeus.utils.JsonResult;

/**
 * 退款/退货Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhRefundService{

	PhRefund save(PhRefund phRefund);
	
	PhRefund findOne(Long id);

	void delete(Long id);

	JsonResult apply(RefundDto refundDto) throws Exception;

	JsonResult init(String orderNo);

	JsonResult info(Long id);
}
