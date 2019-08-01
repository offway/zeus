package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhRefundOrderGoodsService;

import cn.offway.zeus.domain.PhRefundOrderGoods;
import cn.offway.zeus.repository.PhRefundOrderGoodsRepository;


/**
 * 退换货后的订单商品Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-08-01 14:42:53 Exp $
 */
@Service
public class PhRefundOrderGoodsServiceImpl implements PhRefundOrderGoodsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhRefundOrderGoodsRepository phRefundOrderGoodsRepository;
	
	@Override
	public PhRefundOrderGoods save(PhRefundOrderGoods phRefundOrderGoods){
		return phRefundOrderGoodsRepository.save(phRefundOrderGoods);
	}
	
	@Override
	public PhRefundOrderGoods findById(Long id){
		Optional<PhRefundOrderGoods> optional =  phRefundOrderGoodsRepository.findById(id);
		return optional.isPresent()?optional.get():null;

	}

	@Override
	public void delete(Long id){
		phRefundOrderGoodsRepository.deleteById(id);
	}

	@Override
	public List<PhRefundOrderGoods> saveAll(List<PhRefundOrderGoods> entities){
		return phRefundOrderGoodsRepository.saveAll(entities);
	}
}
