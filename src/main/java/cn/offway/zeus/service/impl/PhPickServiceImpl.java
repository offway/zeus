package cn.offway.zeus.service.impl;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhPick;
import cn.offway.zeus.repository.PhPickRepository;
import cn.offway.zeus.service.PhPickService;

import java.util.Optional;


/**
 * OFFWAY优选Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhPickServiceImpl implements PhPickService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhPickRepository phPickRepository;
	
	@Override
	public PhPick save(PhPick phPick){
		return phPickRepository.save(phPick);
	}
	
	@Override
	public PhPick findById(Long id){
		Optional<PhPick> optional = phPickRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
}
