package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhVoucherInfoService;

import cn.offway.zeus.domain.PhVoucherInfo;
import cn.offway.zeus.repository.PhVoucherInfoRepository;


/**
 * 优惠券Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhVoucherInfoServiceImpl implements PhVoucherInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhVoucherInfoRepository phVoucherInfoRepository;
	
	@Override
	public PhVoucherInfo save(PhVoucherInfo phVoucherInfo){
		return phVoucherInfoRepository.save(phVoucherInfo);
	}
	
	@Override
	public PhVoucherInfo findOne(Long id){
		return phVoucherInfoRepository.findOne(id);
	}
}
