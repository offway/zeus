package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhVoucherProjectService;

import cn.offway.zeus.domain.PhVoucherProject;
import cn.offway.zeus.repository.PhVoucherProjectRepository;


/**
 * 优惠券方案Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhVoucherProjectServiceImpl implements PhVoucherProjectService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhVoucherProjectRepository phVoucherProjectRepository;
	
	@Override
	public PhVoucherProject save(PhVoucherProject phVoucherProject){
		return phVoucherProjectRepository.save(phVoucherProject);
	}
	
	@Override
	public PhVoucherProject findById(Long id){
		Optional<PhVoucherProject> optional = phVoucherProjectRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public List<PhVoucherProject> findUseByMerchant(Long merchantId,Double amount){
		return phVoucherProjectRepository.findUseByMerchant(merchantId, amount);
	}
}
