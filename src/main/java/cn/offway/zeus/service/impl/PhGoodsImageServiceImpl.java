package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhGoodsImageService;

import cn.offway.zeus.domain.PhGoodsImage;
import cn.offway.zeus.repository.PhGoodsImageRepository;


/**
 * 商品图片Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhGoodsImageServiceImpl implements PhGoodsImageService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhGoodsImageRepository phGoodsImageRepository;
	
	@Override
	public PhGoodsImage save(PhGoodsImage phGoodsImage){
		return phGoodsImageRepository.save(phGoodsImage);
	}
	
	@Override
	public PhGoodsImage findById(Long id){
		Optional<PhGoodsImage> optional = phGoodsImageRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public List<PhGoodsImage> findByGoodsId(Long goodsId){
		return phGoodsImageRepository.findByGoodsId(goodsId);
	}
	
	@Override
	public List<String> findByGoodsId(Long goodsId,String type){
		return phGoodsImageRepository.findByGoodsId(goodsId, type);
	}
}
