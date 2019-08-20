package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import cn.offway.zeus.config.BitPredicate;
import cn.offway.zeus.config.DiscountPredicate;
import cn.offway.zeus.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import cn.offway.zeus.dto.GoodsDto;
import cn.offway.zeus.repository.PhGoodsRepository;
import cn.offway.zeus.service.PhGoodsService;


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
	public PhGoods findById(Long id){
		Optional<PhGoods> optional = phGoodsRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public List<PhGoods> indexData(){
		return phGoodsRepository.indexData();
	}
	
	@Override
	public List<PhGoods> findBrandRecommend(Long brandId){
		return phGoodsRepository.findBrandRecommend(brandId);
	}
	
	@Override
	public List<PhGoods> findByIds(List<String> ids){
		return phGoodsRepository.findByIds(ids);
	}
	
	@Override
	public List<PhGoods> findRecommend(Long id){
		return phGoodsRepository.findRecommend(id);
	}
	
	@Override
	public int updateViewCount(Long id){
		return phGoodsRepository.updateViewCount(id);
	}
	
	@Override
	public int updateSort(){
		return phGoodsRepository.updateSort();
	}
	
	@Override
	public List<String> searchCategory(String brandName){
		return phGoodsRepository.searchCategory(brandName);
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
				
				String name = goodsDto.getName();
				if (StringUtils.isNotBlank(name)) {
					String[] names = name.split(" ");
					if (names.length == 1) {
						params.add(criteriaBuilder.or(criteriaBuilder.like(root.get("name"),"%" + name + "%"),
								criteriaBuilder.like(root.get("brandName"),"%" + name + "%")));
					} else {
						params.add(criteriaBuilder.or(criteriaBuilder.and(
								criteriaBuilder.like(root.get("brandName"), "%" + names[0] + "%"),
								criteriaBuilder.like(root.get("category"), "%" + names[1] + "%")),
								criteriaBuilder.like(root.get("name"),
										"%" + name.replaceAll(" ", "%") + "%")
								));
					}
				}
				
				if(null != goodsDto.getBrandId()){
					params.add(criteriaBuilder.equal(root.get("brandId"), goodsDto.getBrandId()));
				}
				
				if(StringUtils.isNotBlank(goodsDto.getCategory())){
					params.add(criteriaBuilder.equal(root.get("category"), goodsDto.getCategory()));
				}
				
				if(StringUtils.isNotBlank(goodsDto.getStyle())){
					params.add(criteriaBuilder.like(root.get("style"), "%"+goodsDto.getStyle()+"%"));

				}
				
				if(null != goodsDto.getMerchantId()){
					params.add(criteriaBuilder.equal(root.get("merchantId"), goodsDto.getMerchantId()));
				}
				
				String type = goodsDto.getType();
				if(StringUtils.isNotBlank(type)){
					In<String> in = criteriaBuilder.in(root.get("type"));
					if("男装".equals(type) || "女装".equals(type)){
						in.value("男装＆女装");
					}
					in.value(goodsDto.getType());
					params.add(in);
				}
				
				if(StringUtils.isNotBlank(goodsDto.getBrandType())){
					Subquery<PhBrand> subquery = criteriaQuery.subquery(PhBrand.class);
					Root<PhBrand> subRoot = subquery.from(PhBrand.class);
					subquery.select(subRoot);
					subquery.where(
							criteriaBuilder.equal(root.get("brandId"), subRoot.get("id")),
							criteriaBuilder.equal(subRoot.get("type"), goodsDto.getBrandType())
							);
					params.add(criteriaBuilder.exists(subquery));
				}
				
				if(null != goodsDto.getPickId()){
					Subquery<PhPickGoods> subquery = criteriaQuery.subquery(PhPickGoods.class);
					Root<PhPickGoods> subRoot = subquery.from(PhPickGoods.class);
					subquery.select(subRoot);
					subquery.where(
							criteriaBuilder.equal(root.get("id"), subRoot.get("goodsId")),
							criteriaBuilder.equal(subRoot.get("pickId"), goodsDto.getPickId())
							);
					params.add(criteriaBuilder.exists(subquery));
				}

				if(null != goodsDto.getPromotionId()){
					Subquery<PhPromotionGoods> subquery = criteriaQuery.subquery(PhPromotionGoods.class);
					Root<PhPromotionGoods> subRoot = subquery.from(PhPromotionGoods.class);
					subquery.select(subRoot);
					subquery.where(
							criteriaBuilder.equal(root.get("id"), subRoot.get("goodsId")),
							criteriaBuilder.equal(subRoot.get("promotionId"), goodsDto.getPromotionId())
					);
					params.add(criteriaBuilder.exists(subquery));
				}

				if(null != goodsDto.getDiscount()){
					params.add(new DiscountPredicate((CriteriaBuilderImpl)criteriaBuilder,root.get("originalPrice"),root.get("price"),goodsDto.getDiscount())) ;
				}
				
				
				params.add(criteriaBuilder.equal(root.get("status"),  "1"));

				//TODO chillhigh活动
				params.add(criteriaBuilder.notEqual(root.get("id"),  4465L));

				params.add(criteriaBuilder.equal(root.get("label"),  "0"));

				/*Subquery<PhLimitedSale> subquery = criteriaQuery.subquery(PhLimitedSale.class);
				Root<PhLimitedSale> subRoot = subquery.from(PhLimitedSale.class);
				subquery.select(subRoot);
				subquery.where(criteriaBuilder.equal(root.get("id"), subRoot.get("goodsId")));
				params.add(criteriaBuilder.not(criteriaBuilder.exists(subquery)));*/
				
                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
				return null;
			}
		}, page);
	}

	@Override
	public int countByIdsAndStatus(List<Long> ids,String status){
		return phGoodsRepository.countByIdInAndStatus(ids,status);
	}
}
