package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhLaborLuckyService;

import cn.offway.zeus.domain.PhLaborLucky;
import cn.offway.zeus.repository.PhLaborLuckyRepository;


/**
 * 劳动节活动中奖记录Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhLaborLuckyServiceImpl implements PhLaborLuckyService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhLaborLuckyRepository phLaborLuckyRepository;
	
	@Override
	public PhLaborLucky save(PhLaborLucky phLaborLucky){
		return phLaborLuckyRepository.save(phLaborLucky);
	}
	
	@Override
	public PhLaborLucky findById(Long id){
		Optional<PhLaborLucky> optional = phLaborLuckyRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public List<PhLaborLucky> findByUserIdOrderByCreateTimeDesc(Long userId){
		return phLaborLuckyRepository.findByUserIdOrderByCreateTimeDesc(userId);
	}
	
	@Override
	public List<PhLaborLucky> findByOrderByCreateTimeDesc(){
		return phLaborLuckyRepository.findByOrderByCreateTimeDesc();
	}
}
