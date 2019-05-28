package cn.offway.zeus.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.offway.zeus.domain.PhBrand;
import cn.offway.zeus.dto.BrandDto;

/**
 * 品牌库Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhBrandService{

	PhBrand save(PhBrand phBrand);
	
	PhBrand findOne(Long id);

	List<PhBrand> findByIsRecommendOrderBySortAsc(String isRecommend);

	List<PhBrand> findByTypeOrderByNameAsc(String type);

	List<PhBrand> findByNameLike(String name);

	PhBrand findByName(String name);

	Page<PhBrand> findByPage(BrandDto brandDto, String isRecommend, Pageable page);

	List<PhBrand> findAll(String type);
}
