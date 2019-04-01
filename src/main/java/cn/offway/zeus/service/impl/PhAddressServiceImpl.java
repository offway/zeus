package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhAddressService;

import cn.offway.zeus.domain.PhAddress;
import cn.offway.zeus.repository.PhAddressRepository;


/**
 * 地址管理Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhAddressServiceImpl implements PhAddressService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhAddressRepository phAddressRepository;
	
	@Override
	public PhAddress save(PhAddress phAddress){
		return phAddressRepository.save(phAddress);
	}
	
	@Override
	public PhAddress findOne(Long id){
		return phAddressRepository.findOne(id);
	}
}
