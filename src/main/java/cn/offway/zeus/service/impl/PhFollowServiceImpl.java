package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhBrandRecommend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhFollowService;

import cn.offway.zeus.domain.PhFollow;
import cn.offway.zeus.repository.PhFollowRepository;


/**
 * 关注列表Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2020-03-02 13:55:02 Exp $
 */
@Service
public class PhFollowServiceImpl implements PhFollowService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhFollowRepository phFollowRepository;
	
	@Override
	public PhFollow save(PhFollow phFollow){
		return phFollowRepository.save(phFollow);
	}
	
	@Override
	public PhFollow findOne(Long id){
		Optional<PhFollow> optional = phFollowRepository.findById(id);
		if (optional.isPresent()){
			return optional.get();
		}
		return null;
	}

	@Override
	public void delete(Long id){
		phFollowRepository.deleteById(id);
	}

	@Override
	public List<PhFollow> save(List<PhFollow> entities){
		return phFollowRepository.saveAll(entities);
	}
}
