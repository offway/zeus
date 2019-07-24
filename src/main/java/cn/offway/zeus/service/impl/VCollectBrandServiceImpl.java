package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.VCollectBrand;
import cn.offway.zeus.domain.VCollectGoods;
import cn.offway.zeus.repository.VCollectBrandRepository;
import cn.offway.zeus.service.VCollectBrandService;



/**
 * VIEWService接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class VCollectBrandServiceImpl implements VCollectBrandService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private VCollectBrandRepository vCollectBrandRepository;
	
	@Override
	public VCollectBrand save(VCollectBrand vCollectBrand){
		return vCollectBrandRepository.save(vCollectBrand);
	}
	
	@Override
	public VCollectBrand getOne(Long id){
		return vCollectBrandRepository.getOne(id);
	}
	
	@Override
	public Page<VCollectBrand> findByPage(final Long userId,Pageable page){
		return vCollectBrandRepository.findAll(new Specification<VCollectBrand>() {
			
			@Override
			public Predicate toPredicate(Root<VCollectBrand> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				
				
				if(null != userId){
					params.add(criteriaBuilder.equal(root.get("userId"), userId));
				}
				
                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
				return null;
			}
		}, page);
	}
}
