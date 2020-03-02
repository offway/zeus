package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhChannelUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhBrandRecommendService;

import cn.offway.zeus.domain.PhBrandRecommend;
import cn.offway.zeus.repository.PhBrandRecommendRepository;


/**
 * 品牌推荐表Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2020-03-02 12:24:25 Exp $
 */
@Service
public class PhBrandRecommendServiceImpl implements PhBrandRecommendService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhBrandRecommendRepository phBrandRecommendRepository;
	
	@Override
	public PhBrandRecommend save(PhBrandRecommend phBrandRecommend){
		return phBrandRecommendRepository.save(phBrandRecommend);
	}

	@Override
	public PhBrandRecommend findOne(Long id) {
		Optional<PhBrandRecommend> optional = phBrandRecommendRepository.findById(id);
		if (optional.isPresent()){
			return optional.get();
		}
		return null;
	}

	@Override
	public void delete(Long id) {
		phBrandRecommendRepository.deleteById(id);
	}

	@Override
	public List<PhBrandRecommend> save(List<PhBrandRecommend> entities) {
		return phBrandRecommendRepository.saveAll(entities);
	}

	@Override
	public List<PhBrandRecommend> findAllRecommend(){
		return phBrandRecommendRepository.findAllRecommend();
	}
}
