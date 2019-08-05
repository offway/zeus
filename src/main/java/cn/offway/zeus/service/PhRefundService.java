package cn.offway.zeus.service;

import java.util.Map;

import cn.offway.zeus.dto.ExchangeDto;
import cn.offway.zeus.exception.StockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.offway.zeus.domain.PhRefund;
import cn.offway.zeus.dto.RefundDto;
import cn.offway.zeus.utils.JsonResult;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    JsonResult exchangeInit(String orderNo);

    JsonResult init(String orderNo);

	JsonResult exchangeApply(ExchangeDto exchangeDto) throws Exception;

	JsonResult info(Long id);

	Page<PhRefund> findByPage(Long userId, Pageable page);

	String canRefund(String orderNo);

	PhRefund findByOrderNoEnd(String orderNo);
}
