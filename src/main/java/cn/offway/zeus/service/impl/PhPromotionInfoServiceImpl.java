package cn.offway.zeus.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhPromotionInfoService;

import cn.offway.zeus.domain.PhPromotionInfo;
import cn.offway.zeus.repository.PhPromotionInfoRepository;


/**
 * 促销活动Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-07-12 17:38:42 Exp $
 */
@Service
public class PhPromotionInfoServiceImpl implements PhPromotionInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhPromotionInfoRepository phPromotionInfoRepository;
	
	@Override
	public PhPromotionInfo save(PhPromotionInfo phPromotionInfo){
		return phPromotionInfoRepository.save(phPromotionInfo);
	}
	
	@Override
	public PhPromotionInfo getOne(Long id){
		return phPromotionInfoRepository.getOne(id);
	}

	@Override
	public void deleteById(Long id){
		phPromotionInfoRepository.deleteById(id);
	}

	@Override
	public List<PhPromotionInfo> save(List<PhPromotionInfo> entities){
		return phPromotionInfoRepository.saveAll(entities);
	}

	@Override
	public List<PhPromotionInfo> findByMerchantIdAndGoodsId(Long merchantId, List<Long> goodsIds) {
		return phPromotionInfoRepository.findByMerchantIdAndGoodsId(merchantId,goodsIds);
	}

	@Override
	public List<PhPromotionInfo> findByPlatformAndGoodsId(List<Long> goodsIds) {
		return phPromotionInfoRepository.findByPlatformAndGoodsId(goodsIds);
	}
}
