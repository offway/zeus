package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import cn.offway.zeus.config.BitPredicate;
import cn.offway.zeus.domain.PhMerchant;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhLimitedSale;
import cn.offway.zeus.dto.LimitedSaleDto;
import cn.offway.zeus.repository.PhLimitedSaleRepository;
import cn.offway.zeus.service.PhLimitedSaleService;


/**
 * 限量发售Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhLimitedSaleServiceImpl implements PhLimitedSaleService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhLimitedSaleRepository phLimitedSaleRepository;
	
	@Override
	public PhLimitedSale save(PhLimitedSale phLimitedSale){
		return phLimitedSaleRepository.save(phLimitedSale);
	}
	
	@Override
	public PhLimitedSale findById(Long id){
		Optional<PhLimitedSale> optional = phLimitedSaleRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public PhLimitedSale findByGoodsId(Long goodsId){
		return phLimitedSaleRepository.findByGoodsId(goodsId);
	}

	@Override
	public Long findByShow() {
		return phLimitedSaleRepository.findByShow();
	}

	@Override
	public List<PhLimitedSale> findHead(int channel){
		return phLimitedSaleRepository.findHead(channel);
	}

	@Override
	public PhLimitedSale findHeadForEnd(int channel){
		return phLimitedSaleRepository.findHeadForEnd(channel);
	}
	
	@Override
	public Page<PhLimitedSale> findByPage(final LimitedSaleDto limitedSaleDto,Pageable page){
		return phLimitedSaleRepository.findAll(new Specification<PhLimitedSale>() {
			
			@Override
			public Predicate toPredicate(Root<PhLimitedSale> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				
				Date now = new Date();
				
				if(StringUtils.isNotBlank(limitedSaleDto.getType())){
					if("0".equals(limitedSaleDto.getType())){
						//最新发售
						params.add(criteriaBuilder.lessThanOrEqualTo(root.get("beginTime"), now));
						params.add(criteriaBuilder.greaterThan(root.get("endTime"), now));
					}else if("1".equals(limitedSaleDto.getType())){
						//即将发售
						params.add(criteriaBuilder.greaterThan(root.get("beginTime"), now));
					}else{
						params.add(criteriaBuilder.lessThanOrEqualTo(root.get("endTime"), now));

					}
				}

				String channel = limitedSaleDto.getChannel();
				if(StringUtils.isNotBlank(channel)){
					params.add(new BitPredicate((CriteriaBuilderImpl)criteriaBuilder,root.get("channel"), Integer.parseInt(channel,2))) ;
				}


				params.add(criteriaBuilder.equal(root.get("status"), "1"));

				
                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime")));
				return null;
			}
		}, page);
	}
}
