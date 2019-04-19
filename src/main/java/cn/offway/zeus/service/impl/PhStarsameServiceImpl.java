package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaBuilder.In;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhStarsameService;
import cn.offway.zeus.domain.PhOrderInfo;
import cn.offway.zeus.domain.PhStarsame;
import cn.offway.zeus.domain.PhStarsameImage;
import cn.offway.zeus.repository.PhStarsameRepository;


/**
 * 明星同款Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhStarsameServiceImpl implements PhStarsameService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhStarsameRepository phStarsameRepository;
	
	@Override
	public PhStarsame save(PhStarsame phStarsame){
		return phStarsameRepository.save(phStarsame);
	}
	
	@Override
	public PhStarsame findOne(Long id){
		return phStarsameRepository.findOne(id);
	}
	
	@Override
	public List<PhStarsame> indexData(){
		return phStarsameRepository.indexData();
	}
	
	@Override
	public Page<PhStarsame> findByPage(Pageable page){
		return phStarsameRepository.findAll(new Specification<PhStarsame>() {
			
			@Override
			public Predicate toPredicate(Root<PhStarsame> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				
                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get("sort")));
				return null;
			}
		}, page);
	}
}
