package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.criteria.CriteriaBuilder.In;

import cn.offway.zeus.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.VPickGoodsService;
import cn.offway.zeus.dto.GoodsDto;
import cn.offway.zeus.repository.VPickGoodsRepository;


/**
 * VIEWService接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class VPickGoodsServiceImpl implements VPickGoodsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private VPickGoodsRepository vPickGoodsRepository;
	
	@Override
	public VPickGoods save(VPickGoods vPickGoods){
		return vPickGoodsRepository.save(vPickGoods);
	}
	
	@Override
	public VPickGoods findById(Long id){
		Optional<VPickGoods> optional = vPickGoodsRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public Page<VPickGoods> findByPage(final Long pickId,Pageable page){
		return vPickGoodsRepository.findAll(new Specification<VPickGoods>() {
			@Override
			public Predicate toPredicate(Root<VPickGoods> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				
				if(null != pickId){
					params.add(criteriaBuilder.equal(root.get("pickId"), pickId));
				}
				
				params.add(criteriaBuilder.equal(root.get("status"),  "1"));

                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get("pickGoodsId")));
				return null;
			}
		}, page);
	}
}
