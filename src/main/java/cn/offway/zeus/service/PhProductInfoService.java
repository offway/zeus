package cn.offway.zeus.service;

import java.util.List;
import java.util.Map;

import cn.offway.zeus.domain.PhProductInfo;

/**
 * 活动产品表Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhProductInfoService{

	PhProductInfo save(PhProductInfo phProductInfo);
	
	PhProductInfo findOne(Long id);

	Map<String, List<PhProductInfo>> list();

	List<PhProductInfo> findByUnionid(String unionid);
}
