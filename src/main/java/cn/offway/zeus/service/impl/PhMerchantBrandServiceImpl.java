package cn.offway.zeus.service.impl;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhMerchantBrandService;

import cn.offway.zeus.domain.PhMerchantBrand;
import cn.offway.zeus.repository.PhMerchantBrandRepository;

import java.util.Optional;


/**
 * 商户品牌关系Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhMerchantBrandServiceImpl implements PhMerchantBrandService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhMerchantBrandRepository phMerchantBrandRepository;
	
	@Override
	public PhMerchantBrand save(PhMerchantBrand phMerchantBrand){
		return phMerchantBrandRepository.save(phMerchantBrand);
	}
	
	@Override
	public PhMerchantBrand findById(Long id){
		Optional<PhMerchantBrand> optional = phMerchantBrandRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
}
