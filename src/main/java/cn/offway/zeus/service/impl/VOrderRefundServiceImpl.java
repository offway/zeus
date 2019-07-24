package cn.offway.zeus.service.impl;

import cn.offway.zeus.domain.PhPreorderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.VOrderRefundService;

import cn.offway.zeus.domain.VOrderRefund;
import cn.offway.zeus.repository.VOrderRefundRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;


/**
 * VIEWService接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class VOrderRefundServiceImpl implements VOrderRefundService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private VOrderRefundRepository vOrderRefundRepository;
	
	@Override
	public VOrderRefund save(VOrderRefund vOrderRefund){
		return vOrderRefundRepository.save(vOrderRefund);
	}
	
	@Override
	public VOrderRefund getOne(Long id){
		return vOrderRefundRepository.getOne(id);
	}

	@Override
	public Page<VOrderRefund> findByPage(final Long userId, Pageable page){
		return vOrderRefundRepository.findAll(new Specification<VOrderRefund>() {
			@Override
			public Predicate toPredicate(Root<VOrderRefund> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();

				if(null != userId){
					params.add(criteriaBuilder.equal(root.get("userId"), userId));
				}
				Predicate[] predicates = new Predicate[params.size()];
				criteriaQuery.where(params.toArray(predicates));
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime")));
				return null;
			}
		}, page);
	}
}
