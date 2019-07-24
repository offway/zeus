package cn.offway.zeus.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhGoodsCategoryService;

import cn.offway.zeus.domain.PhGoodsCategory;
import cn.offway.zeus.repository.PhGoodsCategoryRepository;


/**
 * 商品类目Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhGoodsCategoryServiceImpl implements PhGoodsCategoryService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhGoodsCategoryRepository phGoodsCategoryRepository;
	
	@Override
	public PhGoodsCategory save(PhGoodsCategory phGoodsCategory){
		return phGoodsCategoryRepository.save(phGoodsCategory);
	}
	
	@Override
	public PhGoodsCategory getOne(Long id){
		return phGoodsCategoryRepository.getOne(id);
	}
	
	@Override
	public List<PhGoodsCategory> findByGoodsTypeNameOrderBySortAsc(String goodsTypeName){
		return phGoodsCategoryRepository.findByGoodsTypeNameOrderBySortAsc(goodsTypeName);
	}
}
