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

import cn.offway.zeus.domain.PhMerchant;
import cn.offway.zeus.service.PhConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhVoucherInfo;
import cn.offway.zeus.domain.PhVoucherProject;
import cn.offway.zeus.dto.VoucherDto;
import cn.offway.zeus.repository.PhVoucherInfoRepository;
import cn.offway.zeus.repository.PhVoucherProjectRepository;
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
	
	@Autowired
	private PhVoucherProjectRepository phVoucherProjectRepository;

	@Autowired
	private PhConfigService phConfigService;

	
	@Override
	public PhVoucherInfo save(PhVoucherInfo phVoucherInfo){
		return phVoucherInfoRepository.save(phVoucherInfo);
	}
	
	@Override
	public PhVoucherInfo findById(Long id){
		Optional<PhVoucherInfo> optional = phVoucherInfoRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public List<PhVoucherInfo> findByUserId(Long userId){
		return phVoucherInfoRepository.findByUserId(userId);
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
	public int updateStatus(Long voucherId,Double amount,Long userId){
		return phVoucherInfoRepository.updateStatus(voucherId,amount,userId);
	}
	
	@Override
	public int updateStatusBym(Long voucherId,Double amount,Long merchant_id,Long userId){
		return phVoucherInfoRepository.updateStatusBym(voucherId, amount, merchant_id,userId);
	}
	
	@Override
	public int give(Long userId,List<String> voucherProjectIds){
		return phVoucherInfoRepository.give(userId, voucherProjectIds);
	}
	
	@Override
	public int giveByTime(Long userId,List<String> voucherProjectIds){
		return phVoucherInfoRepository.giveByTime(userId, voucherProjectIds);
	}
	
	@Override
	public int countByUserIdAndVoucherProjectIdAndStatus(Long userId,Long voucherProjectId,String status){
		return phVoucherInfoRepository.countByUserIdAndVoucherProjectIdAndStatus(userId,voucherProjectId,status);
	}

	@Override
	public int countByUserIdAndVoucherProjectIdInAndStatus(Long userId,List<String> voucherProjectIds,String status){
		return phVoucherInfoRepository.countByUserIdAndVoucherProjectIdInAndStatus(userId,voucherProjectIds,status);
	}
	
	@Override
	public Long findId(Long userId,Long voucherProjectId){
		return phVoucherInfoRepository.findId(userId, voucherProjectId);
	}
	
	
	@Override
	public void giveVoucher(Long userId,List<String> voucherProjectIds){
		List<PhVoucherProject> phVoucherProjects = phVoucherProjectRepository.findByIdIn(voucherProjectIds);
		for (PhVoucherProject phVoucherProject : phVoucherProjects) {
			if(null==phVoucherProject.getValidNum()){
				phVoucherInfoRepository.giveByTime(userId, phVoucherProject.getId());
			}else{
				phVoucherInfoRepository.give(userId, phVoucherProject.getId());
			}
		}
	}
	
	@Override
	public boolean giveVoucher(Long userId,Long voucherProjectId){
		int count = 0;
		PhVoucherProject phVoucherProject = null;
		Optional<PhVoucherProject> optional  = phVoucherProjectRepository.findById(voucherProjectId);
		phVoucherProject = optional.isPresent()?optional.get():null;
		if(null==phVoucherProject.getValidNum()){
			count = phVoucherInfoRepository.giveByTime(userId, voucherProjectId);
		}else{
			count = phVoucherInfoRepository.give(userId, phVoucherProject.getId());
		}
		return count==1;
	}

	@Override
	public boolean giveVoucherByConfig(Long userId, String config){
		try {
			String content = phConfigService.findContentByName(config);
			Long voucherProjectId = Long.parseLong(content);
			return giveVoucher(userId,voucherProjectId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
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
