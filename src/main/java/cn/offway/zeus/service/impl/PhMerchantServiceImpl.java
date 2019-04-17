package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.offway.zeus.service.PhAddressService;
import cn.offway.zeus.service.PhMerchantFareService;
import cn.offway.zeus.service.PhMerchantService;
import cn.offway.zeus.domain.PhAddress;
import cn.offway.zeus.domain.PhMerchant;
import cn.offway.zeus.domain.PhMerchantFare;
import cn.offway.zeus.domain.PhMerchantFareSpecial;
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
		PhAddress phAddress = phAddressService.findOne(addrId);
		PhMerchantFareSpecial phMerchantFareSpecial = phMerchantFareSpecialRepository.findByProvinceAndCityAndCounty(phAddress.getProvince(), phAddress.getCity(), phAddress.getCounty());
		if(null == phMerchantFareSpecial){
			phMerchantFareSpecial = phMerchantFareSpecialRepository.findByProvinceAndCityAndCounty(phAddress.getProvince(), phAddress.getCity(), null);
		}
		if(null == phMerchantFareSpecial){
			phMerchantFareSpecial = phMerchantFareSpecialRepository.findByProvinceAndCityAndCounty(phAddress.getProvince(), null, null);
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
