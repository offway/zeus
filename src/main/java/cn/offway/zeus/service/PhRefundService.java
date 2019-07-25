package cn.offway.zeus.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
	
	PhRefund findById(Long id);

	void deleteById(Long id);

	JsonResult apply(RefundDto refundDto) throws Exception;

	JsonResult init(String orderNo);

	JsonResult info(Long id);

	Page<PhRefund> findByPage(Long userId, Pageable page);

	String canRefund(String orderNo);

	PhRefund findByOrderNoEnd(String orderNo);
}
