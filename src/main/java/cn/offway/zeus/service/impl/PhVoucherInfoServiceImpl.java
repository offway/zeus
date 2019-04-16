package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhVoucherInfo;
import cn.offway.zeus.dto.VoucherDto;
import cn.offway.zeus.repository.PhVoucherInfoRepository;
import cn.offway.zeus.service.PhVoucherInfoService;


/**
 * 优惠券Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhVoucherInfoServiceImpl implements PhVoucherInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhVoucherInfoRepository phVoucherInfoRepository;
	
	@Override
	public PhVoucherInfo save(PhVoucherInfo phVoucherInfo){
		return phVoucherInfoRepository.save(phVoucherInfo);
	}
	
	@Override
	public PhVoucherInfo findOne(Long id){
		return phVoucherInfoRepository.findOne(id);
	}
	
	@Override
	public List<PhVoucherInfo> findByUserIdOrderByCreateTimeDesc(Long userId){
		return phVoucherInfoRepository.findByUserIdOrderByCreateTimeDesc(userId);
	}
	
	@Override
	public List<PhVoucherInfo> findByIdInOrderByCreateTimeDesc(List<Long> ids){
		return phVoucherInfoRepository.findByIdInOrderByCreateTimeDesc(ids);
	}

	@Override
	public List<PhVoucherInfo> findUseByMerchant(Long userId, Long merchantId, Double amount) {
		return phVoucherInfoRepository.findUseByMerchant(userId, merchantId, amount);
	}

	@Override
	public List<PhVoucherInfo> findUseByPlatform(Long userId, Double amount) {
		return phVoucherInfoRepository.findUseByPlatform(userId, amount);
	}

	@Override
	public int countUseByMerchant(Long userId, Long merchantId, Double amount) {
		return phVoucherInfoRepository.countUseByMerchant(userId, merchantId, amount);
	}

	@Override
	public int countUseByPlatform(Long userId, Double amount) {
		return phVoucherInfoRepository.countUseByPlatform(userId, amount);
	}
	
	@Override
	public int updateStatus(Long voucherId,Double amount){
		return phVoucherInfoRepository.updateStatus(voucherId,amount);
	}
	
	@Override
	public int updateStatusBym(Long voucherId,Double amount,Long merchant_id){
		return phVoucherInfoRepository.updateStatusBym(voucherId, amount, merchant_id);
	}
	
	@Override
	public List<PhVoucherInfo> findAll(final VoucherDto voucherDto){
		return phVoucherInfoRepository.findAll(new Specification<PhVoucherInfo>() {
			
			@Override
			public Predicate toPredicate(Root<PhVoucherInfo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				
				if(StringUtils.isNotBlank(voucherDto.getType())){
					params.add(criteriaBuilder.equal(root.get("type"), voucherDto.getType()));
				}
				
				if(null != voucherDto.getMerchantId()){
					params.add(criteriaBuilder.equal(root.get("merchantId"), voucherDto.getMerchantId()));
				}
				
				if(null != voucherDto.getSumAmount()){
					params.add(criteriaBuilder.lessThanOrEqualTo(root.get("usedMinAmount"), voucherDto.getSumAmount()));
				}
				
				if(null != voucherDto.getUserId()){
					params.add(criteriaBuilder.equal(root.get("userId"), voucherDto.getUserId()));
				}
				
				params.add(criteriaBuilder.equal(root.get("status"),  "0"));
				params.add(criteriaBuilder.lessThanOrEqualTo( root.get("beginTime"), new Date()));
				params.add(criteriaBuilder.greaterThanOrEqualTo(root.get("endTime"), new Date()));
				
                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime")));
				return null;
			}
		});
	}
	
	
}
