package cn.offway.zeus.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhPromotionGoodsService;

import cn.offway.zeus.domain.PhPromotionGoods;
import cn.offway.zeus.repository.PhPromotionGoodsRepository;


/**
 * 促销活动对应商品Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-07-12 17:38:42 Exp $
 */
@Service
public class PhPromotionGoodsServiceImpl implements PhPromotionGoodsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhPromotionGoodsRepository phPromotionGoodsRepository;
	
	@Override
	public PhPromotionGoods save(PhPromotionGoods phPromotionGoods){
		return phPromotionGoodsRepository.save(phPromotionGoods);
	}
	
	@Override
	public PhPromotionGoods getOne(Long id){
		return phPromotionGoodsRepository.getOne(id);
	}

	@Override
	public void deleteById(Long id){
		phPromotionGoodsRepository.deleteById(id);
	}

	@Override
	public List<PhPromotionGoods> save(List<PhPromotionGoods> entities){
		return phPromotionGoodsRepository.saveAll(entities);
	}

	@Override
	public Long findPromotionIdByGoodsId(Long goodsId) {
		return phPromotionGoodsRepository.findPromotionIdByGoodsId(goodsId);
	}

	@Override
	public int countByPromotionIdAndGoodsId(Long promotionId, Long goodsId) {
		return phPromotionGoodsRepository.countByPromotionIdAndGoodsId(promotionId,goodsId);
	}
}
