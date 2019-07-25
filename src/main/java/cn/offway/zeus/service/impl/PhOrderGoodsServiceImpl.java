package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhOrderGoodsService;

import cn.offway.zeus.domain.PhOrderGoods;
import cn.offway.zeus.repository.PhOrderGoodsRepository;


/**
 * 订单商品Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhOrderGoodsServiceImpl implements PhOrderGoodsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhOrderGoodsRepository phOrderGoodsRepository;
	
	@Override
	public PhOrderGoods save(PhOrderGoods phOrderGoods){
		return phOrderGoodsRepository.save(phOrderGoods);
	}
	
	@Override
	public PhOrderGoods findById(Long id){
		Optional<PhOrderGoods> optional = phOrderGoodsRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public List<PhOrderGoods> findByOrderNo(String orderNo){
		return phOrderGoodsRepository.findByOrderNo(orderNo);
	}
}
