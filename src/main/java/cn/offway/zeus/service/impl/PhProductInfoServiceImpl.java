package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhProductInfo;
import cn.offway.zeus.dto.ProductInfo;
import cn.offway.zeus.dto.ProductJoin;
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
	public Map<String, List<ProductInfo>> list(String unionid){
		Map<String, List<ProductInfo>> resultMap = new HashMap<>();
		//用户参与的活动
		List<PhProductInfo> phProductInfos = findByUnionid(unionid);
		resultMap.put("current", getProductInfos(phProductInfoRepository.findByNow(),phProductInfos));
		resultMap.put("next", getProductInfos(phProductInfoRepository.findBynext(),phProductInfos));
		resultMap.put("before", getProductInfos(phProductInfoRepository.findByBefore(),phProductInfos));
		return resultMap;
	}

	private List<ProductInfo> getProductInfos(List<PhProductInfo> infos,List<PhProductInfo> phProductInfos) {
		List<ProductInfo> productInfos = new ArrayList<>();
		for (PhProductInfo phProductInfo : infos) {
			ProductInfo productInfo = new ProductInfo();
			BeanUtils.copyProperties(phProductInfo, productInfo);
			for (PhProductInfo product : phProductInfos) {
				if(product.getId().longValue() == productInfo.getId().longValue()){
					productInfo.setIsJoin(true);
				}
			}
			productInfos.add(productInfo);
		}
		return productInfos;
	}
	
	@Override
	public List<PhProductInfo> findByUnionid(String unionid){
		return phProductInfoRepository.findByUnionid(unionid);
	}

	@Override
	public List<ProductJoin> findProductJoinByUnionid(String unionid) {
		return phProductInfoRepository.findProductJoinByUnionid(unionid);
	}
}
