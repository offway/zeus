package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhBrandService;

import cn.offway.zeus.domain.PhBrand;
import cn.offway.zeus.domain.PhGoods;
import cn.offway.zeus.dto.BrandDto;
import cn.offway.zeus.dto.GoodsDto;
import cn.offway.zeus.repository.PhBrandRepository;


/**
 * 品牌库Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhBrandServiceImpl implements PhBrandService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhBrandRepository phBrandRepository;
	
	@Override
	public PhBrand save(PhBrand phBrand){
		return phBrandRepository.save(phBrand);
	}
	
	@Override
	public PhBrand getOne(Long id){
		return phBrandRepository.getOne(id);
	}
	
	@Override
	public List<PhBrand> findByMerchantId(Long merchantId){
		return phBrandRepository.findByMerchantId(merchantId);
	}
	
	@Override
	public List<PhBrand> findAll(final String type){
		return phBrandRepository.findAll(new Specification<PhBrand>() {
			@Override
			public Predicate toPredicate(Root<PhBrand> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				
				if(StringUtils.isNotBlank(type)){
					params.add(criteriaBuilder.equal(root.get("type"), type));
				}
				//已上架
				params.add(criteriaBuilder.equal(root.get("status"), "1"));

                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get("name")));
				return null;
			}
			
		});
	}
	
	@Override
	public List<PhBrand> findByIsRecommendOrderBySortAsc(String isRecommend){
		return phBrandRepository.findByIsRecommendAndStatusOrderBySortAsc(isRecommend,"1");
	}
	
	@Override
	public List<PhBrand> findByTypeOrderByNameAsc(String type){
		return phBrandRepository.findByTypeAndStatusOrderByNameAsc(type,"1");
	}
	
	@Override
	public List<PhBrand> findByNameLike(String name){
		return phBrandRepository.findByNameLike(name);
	}
	
	@Override
	public PhBrand findByName(String name){
		return phBrandRepository.findByName(name);
	}
	
	@Override
	public List<String> findNameLike(String name){
		return phBrandRepository.findNameLike(name);
	}
	
	@Override
	public Page<PhBrand> findByPage(final BrandDto brandDto,final String isRecommend,Pageable page){
		return phBrandRepository.findAll(new Specification<PhBrand>() {
			
			@Override
			public Predicate toPredicate(Root<PhBrand> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				
				/*if(null != brandDto.getId()){
					params.add(criteriaBuilder.equal(root.get("id"), brandDto.getId()));
				}
				
				if(StringUtils.isNotBlank(brandDto.getName())){
					params.add(criteriaBuilder.like(root.get("name"), "%"+brandDto.getName()+"%"));
				}*/
				
				if(StringUtils.isNotBlank(brandDto.getType())){
					/*if("0".equals(brandDto.getType())){
						params.add(criteriaBuilder.isNotNull(root.get("banner")));
					}*/
					params.add(criteriaBuilder.equal(root.get("type"), brandDto.getType()));
				}
				
				if(StringUtils.isNotBlank(isRecommend)){
					params.add(criteriaBuilder.equal(root.get("isRecommend"), isRecommend));
				}

				//已上架
				params.add(criteriaBuilder.equal(root.get("status"), "1"));
				
				
                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get("sort")));
				return null;
			}
		}, page);
	}
}
