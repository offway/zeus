package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhFollow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhCelebrityListService;

import cn.offway.zeus.domain.PhCelebrityList;
import cn.offway.zeus.repository.PhCelebrityListRepository;


/**
 * 明星信息表Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2020-03-02 13:55:02 Exp $
 */
@Service
public class PhCelebrityListServiceImpl implements PhCelebrityListService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhCelebrityListRepository phCelebrityListRepository;
	
	@Override
	public PhCelebrityList save(PhCelebrityList phCelebrityList){
		return phCelebrityListRepository.save(phCelebrityList);
	}
	
	@Override
	public PhCelebrityList findOne(Long id){
		Optional<PhCelebrityList> optional = phCelebrityListRepository.findById(id);
		if (optional.isPresent()){
			return optional.get();
		}
		return null;
	}

	@Override
	public void delete(Long id){
		phCelebrityListRepository.deleteById(id);
	}

	@Override
	public List<PhCelebrityList> save(List<PhCelebrityList> entities){
		return phCelebrityListRepository.saveAll(entities);
	}

	@Override
	public List<PhCelebrityList> findBynNameLike(String name){
		return phCelebrityListRepository.findBynNameLike("%"+name+"%");
	}

	@Override
	public List<PhCelebrityList> finAll(){
		return phCelebrityListRepository.findAll();
	}
}
