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

import cn.offway.zeus.service.PhAddressService;
import cn.offway.zeus.service.PhMerchantFareService;
import cn.offway.zeus.service.PhMerchantService;
import cn.offway.zeus.domain.PhAddress;
import cn.offway.zeus.domain.PhBrand;
import cn.offway.zeus.domain.PhMerchant;
import cn.offway.zeus.domain.PhMerchantFare;
import cn.offway.zeus.domain.PhMerchantFareSpecial;
import cn.offway.zeus.dto.BrandDto;
import cn.offway.zeus.dto.MerchantDto;
import cn.offway.zeus.repository.PhAddressRepository;
import cn.offway.zeus.repository.PhMerchantFareRepository;
import cn.offway.zeus.repository.PhMerchantFareSpecialRepository;
import cn.offway.zeus.repository.PhMerchantRepository;


/**
 * 商户表Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhMerchantServiceImpl implements PhMerchantService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhMerchantRepository phMerchantRepository;
	
	@Autowired
	private PhMerchantFareRepository phMerchantFareRepository;
	
	@Autowired
	private PhAddressService phAddressService;
	
	@Autowired
	private PhMerchantFareSpecialRepository phMerchantFareSpecialRepository;
	
	@Override
	public PhMerchant save(PhMerchant phMerchant){
		return phMerchantRepository.save(phMerchant);
	}
	
	@Override
	public PhMerchant findOne(Long id){
		return phMerchantRepository.findOne(id);
	}
	
	@Override
	public Page<PhMerchant> findByPage(final MerchantDto merchantDto,Pageable page){
		return phMerchantRepository.findAll(new Specification<PhMerchant>() {
			
			@Override
			public Predicate toPredicate(Root<PhMerchant> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				
				if(StringUtils.isNotBlank(merchantDto.getType())){
					params.add(criteriaBuilder.equal(root.get("type"), merchantDto.getType()));
				}
				
                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get("sort")));
				return null;
			}
		}, page);
	}
	
	/**
	 * 计算运费
	 * @param id
	 * @param num
	 * @param addrId
	 * @return
	 */
	@Override
	public double calculateFare(Long id,int num,Long addrId){
		
		double amount = 0D;
		int fareFirstNum = 0;
		int fareNextNum = 0;
		double fareFirstPrice = 0D;
		double fareNextPrice = 0D;
		
		PhMerchant phMerchant = findOne(id);
		if("1".equals(phMerchant.getIsFreeFare())){
			return amount;
		}
		
		PhMerchantFare phMerchantFare = phMerchantFareRepository.findByMerchantIdAndIsDefault(id, "1");
		Long merchantFareId = phMerchantFare.getId();
		PhAddress phAddress = phAddressService.findOne(addrId);
		PhMerchantFareSpecial phMerchantFareSpecial = phMerchantFareSpecialRepository.findByMerchantFareIdAndProvinceAndCityAndCounty(merchantFareId,phAddress.getProvince(), phAddress.getCity(), phAddress.getCounty());
		if(null == phMerchantFareSpecial){
			phMerchantFareSpecial = phMerchantFareSpecialRepository.findByMerchantFareIdAndProvinceAndCityAndCounty(merchantFareId,phAddress.getProvince(), phAddress.getCity(), null);
		}
		if(null == phMerchantFareSpecial){
			phMerchantFareSpecial = phMerchantFareSpecialRepository.findByMerchantFareIdAndProvinceAndCityAndCounty(merchantFareId,phAddress.getProvince(), null, null);
		}
		if(null == phMerchantFareSpecial){
			fareFirstNum = phMerchantFare.getFareFirstNum().intValue();
			fareNextNum = phMerchantFare.getFareNextNum().intValue();
			fareFirstPrice = phMerchantFare.getFareFirstPrice();
			fareNextPrice = phMerchantFare.getFareNextPrice();
		}else{
			fareFirstNum = phMerchantFareSpecial.getFareFirstNum().intValue();
			fareNextNum = phMerchantFareSpecial.getFareNextNum().intValue();
			fareFirstPrice = phMerchantFareSpecial.getFareFirstPrice();
			fareNextPrice = phMerchantFareSpecial.getFareNextPrice();
		}
		
		if(num<fareFirstNum){
			amount = fareFirstPrice;
		}else{
			num -= fareFirstNum;
			amount += fareFirstPrice;
			
			int c = num%fareNextNum == 0 ? (num/fareNextNum) : (num/fareNextNum)+1;
			amount +=c*fareNextPrice;
		}

		return amount;
	}
}
