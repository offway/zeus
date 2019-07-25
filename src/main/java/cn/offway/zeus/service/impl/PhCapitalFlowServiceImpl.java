package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhMerchant;
import cn.offway.zeus.domain.PhStarsame;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.service.PhCapitalFlowService;

import cn.offway.zeus.domain.PhCapitalFlow;
import cn.offway.zeus.exception.StockException;
import cn.offway.zeus.repository.PhCapitalFlowRepository;
import cn.offway.zeus.repository.PhOrderInfoRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


/**
 * 资金流水Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhCapitalFlowServiceImpl implements PhCapitalFlowService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhCapitalFlowRepository phCapitalFlowRepository;
	
	
	@Override
	public PhCapitalFlow save(PhCapitalFlow phCapitalFlow){
		return phCapitalFlowRepository.save(phCapitalFlow);
	}
	
	@Override
	public PhCapitalFlow findById(Long id){
		Optional<PhCapitalFlow> optional = phCapitalFlowRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {Exception.class,StockException.class})
	public void calculateReturnAmount() throws Exception{
		
		phCapitalFlowRepository.insertByReturnAmount();
		phCapitalFlowRepository.updateBalanceByReturnAmount();
	}
	
	@Override
	public List<PhCapitalFlow> findByBusinessTypeAndUserIdOrderByCreateTimeDesc(String businesstype,Long userId){
		return phCapitalFlowRepository.findByBusinessTypeAndUserIdOrderByCreateTimeDesc(businesstype, userId);
	}

	@Override
	public Page<PhCapitalFlow> findByPage(Long userId, Pageable page){
		return phCapitalFlowRepository.findAll(new Specification<PhCapitalFlow>() {

			@Override
			public Predicate toPredicate(Root<PhCapitalFlow> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();

				Predicate[] predicates = new Predicate[params.size()];
				if(null != userId){
					params.add(criteriaBuilder.equal(root.get("userId"), userId));
				}
				criteriaQuery.where(params.toArray(predicates));
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime")));
				return null;
			}
		}, page);
	}
}
