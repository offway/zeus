package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhBanner;

/**
 * Banner管理Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhBannerService{

	PhBanner save(PhBanner phBanner);
	
	PhBanner findOne(Long id);

	List<PhBanner> banners(String position);
}
