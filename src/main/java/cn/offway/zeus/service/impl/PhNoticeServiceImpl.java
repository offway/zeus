package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhNoticeService;
import cn.offway.zeus.domain.PhNotice;
import cn.offway.zeus.repository.PhNoticeRepository;


/**
 * 消息通知Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhNoticeServiceImpl implements PhNoticeService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhNoticeRepository phNoticeRepository;
	
	@Override
	public PhNotice save(PhNotice phNotice){
		return phNoticeRepository.save(phNotice);
	}
	
	@Override
	public PhNotice findOne(Long id){
		return phNoticeRepository.findOne(id);
	}
	
	@Override
	public List<PhNotice> findNoticeIndex(Long userId){
		return phNoticeRepository.findNoticeIndex(userId);
	}
	
	@Override
	public int countByUserIdAndIsRead(Long userId,String isRead){
		return phNoticeRepository.countByUserIdAndIsRead(userId, isRead);
	}
	
	@Override
	public Page<PhNotice> findByPage(final String type,final Long userId,Pageable page){
		return phNoticeRepository.findAll(new Specification<PhNotice>() {
			
			@Override
			public Predicate toPredicate(Root<PhNotice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				
				if(StringUtils.isNotBlank(type)){
					params.add(criteriaBuilder.equal(root.get("type"), type));
				}
				
				if(null != userId){
					params.add(criteriaBuilder.equal(root.get("userId"), userId));
				}
				
                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
				return null;
			}
		}, page);
	}

	@Override
	public int read(String type) {
		return phNoticeRepository.read(type);
	}
}
