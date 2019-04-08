package cn.offway.zeus.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhStarsameImageService;

import cn.offway.zeus.domain.PhStarsameImage;
import cn.offway.zeus.repository.PhStarsameImageRepository;


/**
 * 明星同款banner图片Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhStarsameImageServiceImpl implements PhStarsameImageService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhStarsameImageRepository phStarsameImageRepository;
	
	@Override
	public PhStarsameImage save(PhStarsameImage phStarsameImage){
		return phStarsameImageRepository.save(phStarsameImage);
	}
	
	@Override
	public PhStarsameImage findOne(Long id){
		return phStarsameImageRepository.findOne(id);
	}
	
}
