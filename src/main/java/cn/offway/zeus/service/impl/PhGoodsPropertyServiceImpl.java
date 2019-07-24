package cn.offway.zeus.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhGoodsProperty;
import cn.offway.zeus.repository.PhGoodsPropertyRepository;
import cn.offway.zeus.service.PhGoodsPropertyService;


/**
 * 商品属性Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhGoodsPropertyServiceImpl implements PhGoodsPropertyService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhGoodsPropertyRepository phGoodsPropertyRepository;
	
	@Override
	public PhGoodsProperty save(PhGoodsProperty phGoodsProperty){
		return phGoodsPropertyRepository.save(phGoodsProperty);
	}
	
	@Override
	public PhGoodsProperty getOne(Long id){
		return phGoodsPropertyRepository.getOne(id);
	}

	@Override
	public List<PhGoodsProperty> findByGoodsId(Long goodsId) {
		return phGoodsPropertyRepository.findByGoodsIdOrderBySortAsc(goodsId);
	}
	
	@Override
	public List<PhGoodsProperty> findByGoodsStockIdOrderBySortAsc(Long goodsStockId){
		return phGoodsPropertyRepository.findByGoodsStockIdOrderBySortAsc(goodsStockId);
	}
}
