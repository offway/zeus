package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhStarsameCommentsService;

import cn.offway.zeus.domain.PhStarsameComments;
import cn.offway.zeus.repository.PhStarsameCommentsRepository;


/**
 * 明星同款评论Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-09-18 16:05:55 Exp $
 */
@Service
public class PhStarsameCommentsServiceImpl implements PhStarsameCommentsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhStarsameCommentsRepository phStarsameCommentsRepository;
	
	@Override
	public PhStarsameComments save(PhStarsameComments phStarsameComments){
		return phStarsameCommentsRepository.save(phStarsameComments);
	}
	
	@Override
	public PhStarsameComments findOne(Long id){
		Optional<PhStarsameComments> optional = phStarsameCommentsRepository.findById(id);
		if (optional.isPresent()){
			return optional.get();
		}
		return null;
	}

	@Override
	public void delete(Long id){
		phStarsameCommentsRepository.deleteById(id);
	}

	@Override
	public List<PhStarsameComments> save(List<PhStarsameComments> entities){
		return phStarsameCommentsRepository.saveAll(entities);
	}
}
