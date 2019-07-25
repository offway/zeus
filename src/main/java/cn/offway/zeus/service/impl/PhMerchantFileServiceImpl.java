package cn.offway.zeus.service.impl;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhMerchantFileService;

import cn.offway.zeus.domain.PhMerchantFile;
import cn.offway.zeus.repository.PhMerchantFileRepository;

import java.util.Optional;


/**
 * 商户附件表Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhMerchantFileServiceImpl implements PhMerchantFileService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhMerchantFileRepository phMerchantFileRepository;
	
	@Override
	public PhMerchantFile save(PhMerchantFile phMerchantFile){
		return phMerchantFileRepository.save(phMerchantFile);
	}
	
	@Override
	public PhMerchantFile findById(Long id){
		Optional<PhMerchantFile> optional = phMerchantFileRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
}
