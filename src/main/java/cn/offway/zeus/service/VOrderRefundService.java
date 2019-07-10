package cn.offway.zeus.service;

import cn.offway.zeus.domain.VOrderRefund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * VIEWService接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface VOrderRefundService{

	VOrderRefund save(VOrderRefund vOrderRefund);
	
	VOrderRefund findOne(Long id);

	Page<VOrderRefund> findByPage(Long userId, Pageable page);
}
