package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhPreorderGoodsService;

import cn.offway.zeus.domain.PhPreorderGoods;
import cn.offway.zeus.repository.PhPreorderGoodsRepository;


/**
 * 预生成订单商品Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhPreorderGoodsServiceImpl implements PhPreorderGoodsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhPreorderGoodsRepository phPreorderGoodsRepository;
	
	@Override
	public PhPreorderGoods save(PhPreorderGoods phPreorderGoods){
		return phPreorderGoodsRepository.save(phPreorderGoods);
	}
	
	@Override
	public PhPreorderGoods findOne(Long id){
		return phPreorderGoodsRepository.findOne(id);
	}
}
