package cn.offway.zeus.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhActivityImageService;

import cn.offway.zeus.domain.PhActivityImage;
import cn.offway.zeus.repository.PhActivityImageRepository;


/**
 * 活动图片表-每日福利Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
@Service
public class PhActivityImageServiceImpl implements PhActivityImageService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhActivityImageRepository phActivityImageRepository;
	
	@Override
	public PhActivityImage save(PhActivityImage phActivityImage){
		return phActivityImageRepository.save(phActivityImage);
	}
	
	@Override
	public PhActivityImage getOne(Long id){
		return phActivityImageRepository.getOne(id);
	}
	
	@Override
	public List<PhActivityImage> findByActivityId(Long activityId){
		return phActivityImageRepository.findByActivityId(activityId);
	}
}
