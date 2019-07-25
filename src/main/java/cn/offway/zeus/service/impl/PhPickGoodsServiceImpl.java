package cn.offway.zeus.service.impl;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhPickGoodsService;

import cn.offway.zeus.domain.PhPickGoods;
import cn.offway.zeus.repository.PhPickGoodsRepository;

import java.util.Optional;


/**
 * OFFWAY优选商品明细Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhPickGoodsServiceImpl implements PhPickGoodsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhPickGoodsRepository phPickGoodsRepository;
	
	@Override
	public PhPickGoods save(PhPickGoods phPickGoods){
		return phPickGoodsRepository.save(phPickGoods);
	}
	
	@Override
	public PhPickGoods findById(Long id){
		Optional<PhPickGoods> optional = phPickGoodsRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
}
