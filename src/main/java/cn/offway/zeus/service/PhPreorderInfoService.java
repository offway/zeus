package cn.offway.zeus.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.offway.zeus.domain.PhPreorderInfo;

/**
 * 预生成订单Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhPreorderInfoService{

	PhPreorderInfo save(PhPreorderInfo phPreorderInfo);
	
	PhPreorderInfo getOne(Long id);

	PhPreorderInfo findByOrderNoAndStatus(String orderno, String status);

	void alipay(String status, String preorderNo) throws Exception;

	void wxpay(String status, String preorderNo) throws Exception;

	Page<PhPreorderInfo> findByPage(Long userId, Pageable page);

	int countByUserIdAndStatus(Long userId, String status);

	List<String> orderTimeOut();

	void returnOrder(String preorderNo) throws Exception;

	void cancelOrder(String preorderNo, String remark) throws Exception;
}
