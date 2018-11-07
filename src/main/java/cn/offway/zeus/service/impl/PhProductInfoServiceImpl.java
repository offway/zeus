package cn.offway.zeus.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhProductInfo;
import cn.offway.zeus.repository.PhProductInfoRepository;
import cn.offway.zeus.service.PhProductInfoService;


/**
 * 活动产品表Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
@Service
public class PhProductInfoServiceImpl implements PhProductInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhProductInfoRepository phProductInfoRepository;
	
	@Override
	public PhProductInfo save(PhProductInfo phProductInfo){
		return phProductInfoRepository.save(phProductInfo);
	}
	
	@Override
	public PhProductInfo findOne(Long id){
		return phProductInfoRepository.findOne(id);
	}
	
	@Override
	public Map<String, List<PhProductInfo>> list(){
		Map<String, List<PhProductInfo>> resultMap = new HashMap<>();
		
		resultMap.put("current", phProductInfoRepository.findByNow());
		resultMap.put("next", phProductInfoRepository.findBynext());
		resultMap.put("before", phProductInfoRepository.findByBefore());
		return resultMap;
	}
	
	@Override
	public List<PhProductInfo> findByUnionid(String unionid){
		return phProductInfoRepository.findByUnionid(unionid);
	}
}
