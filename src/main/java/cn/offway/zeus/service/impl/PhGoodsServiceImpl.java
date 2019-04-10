package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhGoodsService;
import cn.offway.zeus.domain.PhGoods;
import cn.offway.zeus.dto.GoodsDto;
import cn.offway.zeus.repository.PhGoodsRepository;


/**
 * 商品表Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhGoodsServiceImpl implements PhGoodsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhGoodsRepository phGoodsRepository;
	
	@Override
	public PhGoods save(PhGoods phGoods){
		return phGoodsRepository.save(phGoods);
	}
	
	@Override
	public PhGoods findOne(Long id){
		return phGoodsRepository.findOne(id);
	}
	
	@Override
	public List<PhGoods> indexData(){
		return phGoodsRepository.indexData();
	}
	
	@Override
	public List<PhGoods> findRecommend(Long id){
		return phGoodsRepository.findRecommend(id);
	}
	
	@Override
	public Page<PhGoods> findByPage(final GoodsDto goodsDto,Pageable page){
		return phGoodsRepository.findAll(new Specification<PhGoods>() {
			
			@Override
			public Predicate toPredicate(Root<PhGoods> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				
				
				if(StringUtils.isNotBlank(goodsDto.getBrandName())){
					params.add(criteriaBuilder.like(root.get("brandName"), "%"+goodsDto.getBrandName()+"%"));
				}
				
				if(StringUtils.isNotBlank(goodsDto.getName())){
					params.add(criteriaBuilder.like(root.get("name"), "%"+goodsDto.getName()+"%"));
				}
				
				if(null != goodsDto.getBrandId()){
					params.add(criteriaBuilder.equal(root.get("brandId"), goodsDto.getBrandId()));
				}
				
				if(StringUtils.isNotBlank(goodsDto.getCategory())){
					params.add(criteriaBuilder.equal(root.get("category"), goodsDto.getCategory()));
				}
				
				if(StringUtils.isNotBlank(goodsDto.getType())){
					params.add(criteriaBuilder.equal(root.get("type"), goodsDto.getType()));
				}
				
				params.add(criteriaBuilder.equal(root.get("status"),  "1"));
				
                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
				return null;
			}
		}, page);
	}
}
