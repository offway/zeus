package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhGoodsTypeService;

import cn.offway.zeus.domain.PhGoodsType;
import cn.offway.zeus.repository.PhGoodsTypeRepository;


/**
 * 商品类别Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhGoodsTypeServiceImpl implements PhGoodsTypeService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhGoodsTypeRepository phGoodsTypeRepository;
	
	@Override
	public PhGoodsType save(PhGoodsType phGoodsType){
		return phGoodsTypeRepository.save(phGoodsType);
	}
	
	@Override
	public PhGoodsType findOne(Long id){
		return phGoodsTypeRepository.findOne(id);
	}
}
