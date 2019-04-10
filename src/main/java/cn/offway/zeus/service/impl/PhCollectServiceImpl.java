package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.service.PhCollectService;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.domain.PhCollect;
import cn.offway.zeus.repository.PhCollectRepository;


/**
 * 收藏夹Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhCollectServiceImpl implements PhCollectService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhCollectRepository phCollectRepository;
	
	@Autowired
	private PhUserInfoService phUserInfoService;
	
	@Override
	public PhCollect save(PhCollect phCollect){
		return phCollectRepository.save(phCollect);
	}
	
	@Override
	public PhCollect findOne(Long id){
		return phCollectRepository.findOne(id);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = Exception.class)
	public void collect(Long userId,String type,Long matchId) throws Exception{
		int count = phCollectRepository.insert(userId, type, matchId);
		if(count>0){
			phUserInfoService.updateCollect(userId);
		}
	}
}
