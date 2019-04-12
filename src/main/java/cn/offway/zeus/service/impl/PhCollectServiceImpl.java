package cn.offway.zeus.service.impl;

import java.util.Date;

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
	public void delete(Long id){
		phCollectRepository.delete(id);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = Exception.class)
	public Long collect(Long userId,String type,Long matchId) throws Exception{
		PhCollect phCollect = new PhCollect();
		phCollect.setCreateTime(new Date());
		phCollect.setMatchId(matchId);
		phCollect.setType(type);
		phCollect.setUserId(userId);
		phCollect = save(phCollect);
		phUserInfoService.updateCollect(userId);
		return phCollect.getId();
	}
	
	@Override
	public Long isCollect(Long userId,String type,Long matchId){
		PhCollect phCollect = phCollectRepository.findByUserIdAndTypeAndMatchId(userId, type, matchId);
		if(null !=phCollect){
			return phCollect.getId();
		}
		return null;

	}
}
