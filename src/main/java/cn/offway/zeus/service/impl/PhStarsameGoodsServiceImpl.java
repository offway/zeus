package cn.offway.zeus.service.impl;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhStarsameGoodsService;

import cn.offway.zeus.domain.PhStarsameGoods;
import cn.offway.zeus.repository.PhStarsameGoodsRepository;

import java.util.Optional;


/**
 * 明星同款商品Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhStarsameGoodsServiceImpl implements PhStarsameGoodsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhStarsameGoodsRepository phStarsameGoodsRepository;
	
	@Override
	public PhStarsameGoods save(PhStarsameGoods phStarsameGoods){
		return phStarsameGoodsRepository.save(phStarsameGoods);
	}
	
	@Override
	public PhStarsameGoods findById(Long id){
		Optional<PhStarsameGoods> optional = phStarsameGoodsRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
}
