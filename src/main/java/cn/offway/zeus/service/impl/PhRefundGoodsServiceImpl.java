package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhRefundGoodsService;

import cn.offway.zeus.domain.PhRefundGoods;
import cn.offway.zeus.repository.PhRefundGoodsRepository;


/**
 * 退款/退货商品明细Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhRefundGoodsServiceImpl implements PhRefundGoodsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhRefundGoodsRepository phRefundGoodsRepository;
	
	@Override
	public PhRefundGoods save(PhRefundGoods phRefundGoods){
		return phRefundGoodsRepository.save(phRefundGoods);
	}
	
	@Override
	public PhRefundGoods findById(Long id){
		Optional<PhRefundGoods> optional = phRefundGoodsRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public List<PhRefundGoods> save(List<PhRefundGoods> phRefundGoodss){
		return phRefundGoodsRepository.saveAll(phRefundGoodss);
	}

	@Override
	public Long refundGoodsCount(Long orderGoodsId) {
		return phRefundGoodsRepository.refundGoodsCount(orderGoodsId);
	}
	
	@Override
	public int deleteByRefundId(Long refundId){
		return phRefundGoodsRepository.deleteByRefundId(refundId);
	}

	@Override
	public List<PhRefundGoods> findByRefundId(Long refundId) {
		return phRefundGoodsRepository.findByRefundId(refundId);
	}
}
