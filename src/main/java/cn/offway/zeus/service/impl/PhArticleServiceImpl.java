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

import cn.offway.zeus.service.PhArticleService;

import cn.offway.zeus.domain.PhArticle;
import cn.offway.zeus.domain.PhBrand;
import cn.offway.zeus.dto.ArticleDto;
import cn.offway.zeus.dto.BrandDto;
import cn.offway.zeus.repository.PhArticleRepository;


/**
 * 文章Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhArticleServiceImpl implements PhArticleService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhArticleRepository phArticleRepository;
	
	@Override
	public PhArticle save(PhArticle phArticle){
		return phArticleRepository.save(phArticle);
	}
	
	@Override
	public PhArticle findOne(Long id){
		return phArticleRepository.findOne(id);
	}
	
	@Override
	public List<PhArticle> findRecommendByType(String type,int limit,Long id){
		return phArticleRepository.findRecommendByType(type, limit,id);
	}
	
	@Override
	public List<PhArticle> findRecommendByTypeAndTag(String type, int limit,Long id,String tag){
		return phArticleRepository.findRecommendByTypeAndTag(type, limit, id, tag);
	}

	
	@Override
	public Page<PhArticle> findByPage(final ArticleDto articleDto,Pageable page){
		return phArticleRepository.findAll(new Specification<PhArticle>() {
			
			@Override
			public Predicate toPredicate(Root<PhArticle> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				
				if(StringUtils.isNotBlank(articleDto.getType())){
					params.add(criteriaBuilder.equal(root.get("type"), articleDto.getType()));
				}
				
				if(StringUtils.isNotBlank(articleDto.getTag())){
					params.add(criteriaBuilder.like(root.get("tag"), "%"+articleDto.getTag()+"%"));
				}
				
				if(StringUtils.isNotBlank(articleDto.getWd())){
					params.add(criteriaBuilder.or(
							criteriaBuilder.like(root.get("title"), "%"+articleDto.getWd()+"%"),
							criteriaBuilder.like(root.get("tag"), "%"+articleDto.getWd()+"%")) );
				}
				
				params.add(criteriaBuilder.equal(root.get("status"), "1"));

				
                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("approval")));
				return null;
			}
		}, page);
	}
}
