package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaBuilder.In;

import cn.offway.zeus.domain.PhMerchant;
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
	public PhStarsame findById(Long id){
		Optional<PhStarsame> optional = phStarsameRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public List<PhStarsame> indexData(){
		return phStarsameRepository.indexData();
	}
	
	@Override
	public int praise(Long id){
		return phStarsameRepository.praise(id);
	}
	
	@Override
	public Page<PhStarsame> findByPage(String starName,Pageable page){
		return phStarsameRepository.findAll(new Specification<PhStarsame>() {
			
			@Override
			public Predicate toPredicate(Root<PhStarsame> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				
                Predicate[] predicates = new Predicate[params.size()];
                if(StringUtils.isNotBlank(starName)){
                	params.add(criteriaBuilder.like(root.get("starName"), "%"+starName+"%"));
                }
				params.add(criteriaBuilder.equal(root.get("sort"), "999"));
                criteriaQuery.where(params.toArray(predicates));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime")));
				return null;
			}
		}, page);
	}

	@Override
	public Page<PhStarsame> findByPage(String starName, Pageable page, String sortName){
		return phStarsameRepository.findAll(new Specification<PhStarsame>() {

			@Override
			public Predicate toPredicate(Root<PhStarsame> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();

				Predicate[] predicates = new Predicate[params.size()];
				if(StringUtils.isNotBlank(starName)){
					params.add(criteriaBuilder.like(root.get("starName"), "%"+starName+"%"));
				}
				if (!"sortMini".equals(sortName)){
					params.add(criteriaBuilder.equal(root.get("sort"), "999"));
				}

				criteriaQuery.where(params.toArray(predicates));
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime")));
				return null;
			}
		}, page);
	}

	@Override
	public int praisecancel(Long id) {
		return phStarsameRepository.praisecancel(id);
	}
}
