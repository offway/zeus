package cn.offway.zeus.service.impl;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhGoodsSpecialService;

import cn.offway.zeus.domain.PhGoodsSpecial;
import cn.offway.zeus.repository.PhGoodsSpecialRepository;

import java.util.Optional;


/**
 * 特殊商品Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhGoodsSpecialServiceImpl implements PhGoodsSpecialService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhGoodsSpecialRepository phGoodsSpecialRepository;
	
	@Override
	public PhGoodsSpecial save(PhGoodsSpecial phGoodsSpecial){
		return phGoodsSpecialRepository.save(phGoodsSpecial);
	}
	
	@Override
	public PhGoodsSpecial findById(Long id){
		Optional<PhGoodsSpecial> optional = phGoodsSpecialRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public int countByGoodsId(Long goodsId){
		return phGoodsSpecialRepository.countByGoodsId(goodsId);
	}
}
