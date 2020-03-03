package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhThemeGoodsService;

import cn.offway.zeus.domain.PhThemeGoods;
import cn.offway.zeus.repository.PhThemeGoodsRepository;


/**
 * 主题商品表Service接口实现
 *
 * @author tbw
 * @version $v: 1.0.0, $time:2020-03-03 13:55:07 Exp $
 */
@Service
public class PhThemeGoodsServiceImpl implements PhThemeGoodsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhThemeGoodsRepository phThemeGoodsRepository;
	
	@Override
	public PhThemeGoods save(PhThemeGoods phThemeGoods){
		return phThemeGoodsRepository.save(phThemeGoods);
	}
	
	@Override
	public PhThemeGoods findOne(Long id){
		Optional<PhThemeGoods> optional = phThemeGoodsRepository.findById(id);
		if (optional.isPresent()){
			return optional.get();
		}
		return null;
	}

	@Override
	public void delete(Long id){
		phThemeGoodsRepository.deleteById(id);
	}

	@Override
	public List<PhThemeGoods> save(List<PhThemeGoods> entities){
		return phThemeGoodsRepository.saveAll(entities);
	}
}
