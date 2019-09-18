package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhActivityCommentsService;

import cn.offway.zeus.domain.PhActivityComments;
import cn.offway.zeus.repository.PhActivityCommentsRepository;


/**
 * 文章评论Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-09-18 16:05:55 Exp $
 */
@Service
public class PhActivityCommentsServiceImpl implements PhActivityCommentsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhActivityCommentsRepository phActivityCommentsRepository;
	
	@Override
	public PhActivityComments save(PhActivityComments phActivityComments){
		return phActivityCommentsRepository.save(phActivityComments);
	}
	
	@Override
	public PhActivityComments findOne(Long id){
		Optional<PhActivityComments> optional = phActivityCommentsRepository.findById(id);
		if (optional.isPresent()){
			return optional.get();
		}
		return null;
	}

	@Override
	public void delete(Long id){
		phActivityCommentsRepository.deleteById(id);
	}

	@Override
	public List<PhActivityComments> save(List<PhActivityComments> entities){
		return phActivityCommentsRepository.saveAll(entities);
	}
}
