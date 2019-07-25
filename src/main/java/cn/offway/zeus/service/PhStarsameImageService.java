package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhStarsameImage;

/**
 * 明星同款banner图片Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhStarsameImageService{

	PhStarsameImage save(PhStarsameImage phStarsameImage);
	
	PhStarsameImage findById(Long id);

}
