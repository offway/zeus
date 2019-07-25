package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import cn.offway.zeus.domain.PhMerchant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhGoods;
import cn.offway.zeus.domain.VCollectGoods;
import cn.offway.zeus.dto.GoodsDto;
import cn.offway.zeus.repository.VCollectGoodsRepository;
import cn.offway.zeus.service.VCollectGoodsService;


/**
 * VIEWService接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class VCollectGoodsServiceImpl implements VCollectGoodsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private VCollectGoodsRepository vCollectGoodsRepository;
	
	@Override
	public VCollectGoods save(VCollectGoods vCollectGoods){
		return vCollectGoodsRepository.save(vCollectGoods);
	}
	
	@Override
	public VCollectGoods findById(Long id){
		Optional<VCollectGoods> optional = vCollectGoodsRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public Page<VCollectGoods> findByPage(final Long userId,Pageable page){
		return vCollectGoodsRepository.findAll(new Specification<VCollectGoods>() {
			
			@Override
			public Predicate toPredicate(Root<VCollectGoods> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
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
