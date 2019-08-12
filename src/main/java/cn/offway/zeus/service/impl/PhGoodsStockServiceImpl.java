package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhGoodsStock;
import cn.offway.zeus.repository.PhGoodsStockRepository;
import cn.offway.zeus.service.PhGoodsStockService;


/**
 * 商品库存Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhGoodsStockServiceImpl implements PhGoodsStockService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhGoodsStockRepository phGoodsStockRepository;
	
	@Override
	public PhGoodsStock save(PhGoodsStock phGoodsStock){
		return phGoodsStockRepository.save(phGoodsStock);
	}

	@Override
	public int countByIdAndStockIsLessThan(Long id,Long num){
		return phGoodsStockRepository.countByIdAndStockIsLessThan(id, num);
	}
	
	@Override
	public PhGoodsStock findById(Long id){
		Optional<PhGoodsStock> optional = phGoodsStockRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public List<PhGoodsStock> findByIdIn(Set<Long> ids){
		return phGoodsStockRepository.findByIdIn(ids);
	}
	
	@Override
	public List<PhGoodsStock> findByGoodsId(Long goodsId){
		return phGoodsStockRepository.findByGoodsId(goodsId);
	}
	
	@Override
	public int updateStock(Long stockId,Long count){
		return phGoodsStockRepository.updateStock(stockId,count);
	}
	
	@Override
	public int sumStock(Long goodsId){
		return phGoodsStockRepository.sumStock(goodsId);
	}
}
