package cn.offway.zeus.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.offway.zeus.domain.PhOrderInfo;
import cn.offway.zeus.dto.OrderAddDto;
import cn.offway.zeus.utils.JsonResult;

/**
 * 订单Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhOrderInfoService{

	PhOrderInfo save(PhOrderInfo phOrderInfo);
	
	PhOrderInfo findById(Long id);

	String generateOrderNo(String prefix);

	JsonResult add(OrderAddDto orderAddDto) throws Exception;

	Page<PhOrderInfo> findByPage(Long userId, String status, Pageable page);

	List<PhOrderInfo> findByPreorderNoAndStatus(String preorderno, String status);

	PhOrderInfo findByOrderNo(String orderNo);
	
	int countByUserIdAndStatus(Long userId,String status);
}
