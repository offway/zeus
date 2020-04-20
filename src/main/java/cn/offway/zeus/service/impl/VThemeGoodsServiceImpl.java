package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.VPickGoods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.VThemeGoodsService;

import cn.offway.zeus.domain.VThemeGoods;
import cn.offway.zeus.repository.VThemeGoodsRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


/**
 * VIEWService接口实现
 *
 * @author tbw
 * @version $v: 1.0.0, $time:2020-03-04 14:10:13 Exp $
 */
@Service
public class VThemeGoodsServiceImpl implements VThemeGoodsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private VThemeGoodsRepository vThemeGoodsRepository;
	
	@Override
	public VThemeGoods save(VThemeGoods vThemeGoods){
		return vThemeGoodsRepository.save(vThemeGoods);
	}
	
	@Override
	public VThemeGoods findOne(Long id){
		Optional<VThemeGoods> optional = vThemeGoodsRepository.findById(id);
		if (optional.isPresent()){
			return optional.get();
		}
		return null;
	}

	@Override
	public void delete(Long id){
		vThemeGoodsRepository.deleteById(id);
	}

	@Override
	public List<VThemeGoods> save(List<VThemeGoods> entities){
		return vThemeGoodsRepository.saveAll(entities);
	}

	@Override
	public Page<VThemeGoods> findByPage(final Long themeId, Pageable page,int sort){
		return vThemeGoodsRepository.findAll(new Specification<VThemeGoods>() {
			@Override
			public Predicate toPredicate(Root<VThemeGoods> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();

				if(null != themeId){
					params.add(criteriaBuilder.equal(root.get("themeId"), themeId));
				}

				//0-销量，1-人气，2-新品，3-价格降序，4-价格升序，5-默认
				switch (sort){
					case 0:
						criteriaQuery.orderBy(criteriaBuilder.desc(root.get("saleCount")));
						break;
					case 1:
						criteriaQuery.orderBy(criteriaBuilder.desc(root.get("viewCount")));
						break;
					case 2:
						criteriaQuery.orderBy(criteriaBuilder.desc(root.get("upTime")));
						break;
					case 3:
						criteriaQuery.orderBy(criteriaBuilder.desc(root.get("price")));
						break;
					case 4:
						criteriaQuery.orderBy(criteriaBuilder.asc(root.get("price")));
						break;
					case 5:
						criteriaQuery.orderBy(criteriaBuilder.asc(root.get("themeGoodsId")));
						break;
				}

				params.add(criteriaBuilder.equal(root.get("status"),  "1"));

				Predicate[] predicates = new Predicate[params.size()];
				criteriaQuery.where(params.toArray(predicates));
				return null;
			}
		},page);
	}

	@Override
	public List<VThemeGoods> findAllTop10(Long id){
		return vThemeGoodsRepository.findAllTop10(id);
	}
}
