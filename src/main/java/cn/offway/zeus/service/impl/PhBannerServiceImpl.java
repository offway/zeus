package cn.offway.zeus.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhBannerService;

import cn.offway.zeus.domain.PhBanner;
import cn.offway.zeus.repository.PhBannerRepository;


/**
 * Banner管理Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhBannerServiceImpl implements PhBannerService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhBannerRepository phBannerRepository;
	
	@Override
	public PhBanner save(PhBanner phBanner){
		return phBannerRepository.save(phBanner);
	}
	
	@Override
	public PhBanner getOne(Long id){
		return phBannerRepository.getOne(id);
	}
	
	@Override
	public List<PhBanner> banners(String position){
		return phBannerRepository.banners(position);
	}
	
	@Override
	public List<PhBanner> banners(){
		return phBannerRepository.banners();
	}
}
