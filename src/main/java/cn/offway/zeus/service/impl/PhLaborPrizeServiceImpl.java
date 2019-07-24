package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhLaborPrizeService;

import cn.offway.zeus.domain.PhLaborPrize;
import cn.offway.zeus.repository.PhLaborPrizeRepository;


/**
 * 劳动节活动奖品Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhLaborPrizeServiceImpl implements PhLaborPrizeService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhLaborPrizeRepository phLaborPrizeRepository;
	
	@Override
	public PhLaborPrize save(PhLaborPrize phLaborPrize){
		return phLaborPrizeRepository.save(phLaborPrize);
	}
	
	@Override
	public PhLaborPrize getOne(Long id){
		return phLaborPrizeRepository.getOne(id);
	}
}
