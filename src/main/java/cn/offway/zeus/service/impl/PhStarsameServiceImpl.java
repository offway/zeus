package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhStarsameService;

import cn.offway.zeus.domain.PhStarsame;
import cn.offway.zeus.repository.PhStarsameRepository;


/**
 * 明星同款Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhStarsameServiceImpl implements PhStarsameService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhStarsameRepository phStarsameRepository;
	
	@Override
	public PhStarsame save(PhStarsame phStarsame){
		return phStarsameRepository.save(phStarsame);
	}
	
	@Override
	public PhStarsame findOne(Long id){
		return phStarsameRepository.findOne(id);
	}
}
