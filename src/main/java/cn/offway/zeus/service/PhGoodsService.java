package cn.offway.zeus.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.offway.zeus.domain.PhGoods;
import cn.offway.zeus.dto.GoodsDto;

/**
 * 商品表Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhGoodsService{

	PhGoods save(PhGoods phGoods);
	
	PhGoods getOne(Long id);

	List<PhGoods> indexData();

	Page<PhGoods> findByPage(GoodsDto goodsDto, Pageable page);

	List<PhGoods> findRecommend(Long id);

	int updateViewCount(Long id);

	List<PhGoods> findBrandRecommend(Long brandId);

	List<PhGoods> findByIds(List<String> ids);

	int updateSort();

	List<String> searchCategory(String brandName);
}
