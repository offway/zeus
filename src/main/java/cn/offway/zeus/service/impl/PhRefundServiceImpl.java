package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhRefundService;

import cn.offway.zeus.domain.PhRefund;
import cn.offway.zeus.repository.PhRefundRepository;


/**
 * 退款/退货Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhRefundServiceImpl implements PhRefundService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhRefundRepository phRefundRepository;
	
	@Override
	public PhRefund save(PhRefund phRefund){
		return phRefundRepository.save(phRefund);
	}
	
	@Override
	public PhRefund findOne(Long id){
		return phRefundRepository.findOne(id);
	}
}
