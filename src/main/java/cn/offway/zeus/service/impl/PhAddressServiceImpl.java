package cn.offway.zeus.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhAddress;
import cn.offway.zeus.repository.PhAddressRepository;
import cn.offway.zeus.service.PhAddressService;


/**
 * 地址管理Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-02-12 11:26:00 Exp $
 */
@Service
public class PhAddressServiceImpl implements PhAddressService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhAddressRepository phAddressRepository;
	
	@Override
	public PhAddress save(PhAddress phAddress){
		if("1".equals(phAddress.getIsDefault())){
			phAddressRepository.updatePhAddress(phAddress.getUserId()); 
		}
		return phAddressRepository.save(phAddress);
	}
	
	@Override
	public void deleteById(Long id){
		phAddressRepository.deleteById(id);
	}
	
	@Override
	public PhAddress getOne(Long id){
		return phAddressRepository.getOne(id);
	}
	
	@Override
	public List<PhAddress> findByUserId(Long userId){
		return phAddressRepository.findByUserId(userId);
	}
	
	@Override
	public  PhAddress findDefault(Long userId){
		List<PhAddress> phAddresses = phAddressRepository.findByUserIdAndIsDefault(userId, "1");
		if(!phAddresses.isEmpty()){
			return phAddresses.get(0);
		}
		return null;
	}
}
