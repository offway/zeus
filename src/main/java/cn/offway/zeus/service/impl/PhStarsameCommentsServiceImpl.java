package cn.offway.zeus.service.impl;

import cn.offway.zeus.domain.PhStarsameComments;
import cn.offway.zeus.repository.PhStarsameCommentsRepository;
import cn.offway.zeus.service.PhStarsameCommentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * 明星同款评论Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-09-18 16:05:55 Exp $
 */
@Service
public class PhStarsameCommentsServiceImpl implements PhStarsameCommentsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhStarsameCommentsRepository phStarsameCommentsRepository;
	
	@Override
	public PhStarsameComments save(PhStarsameComments phStarsameComments){
		return phStarsameCommentsRepository.save(phStarsameComments);
	}
	
	@Override
	public PhStarsameComments findOne(Long id){
		Optional<PhStarsameComments> optional = phStarsameCommentsRepository.findById(id);
		if (optional.isPresent()){
			return optional.get();
		}
		return null;
	}

	@Override
	public void delete(Long id){
		phStarsameCommentsRepository.deleteById(id);
	}

	@Override
	public Page<PhStarsameComments> findByPage(Long starSameId, Pageable page) {
		return phStarsameCommentsRepository.findAll(new Specification<PhStarsameComments>() {
			@Override
			public Predicate toPredicate(Root<PhStarsameComments> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				Predicate[] predicates = new Predicate[params.size()];
				params.add(criteriaBuilder.equal(root.get("starsameId"), starSameId));
				criteriaQuery.where(params.toArray(predicates));
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime")));
				return null;
			}
		}, page);
	}

	@Override
	public List<PhStarsameComments> save(List<PhStarsameComments> entities){
		return phStarsameCommentsRepository.saveAll(entities);
	}
}
