package cn.offway.zeus.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.offway.zeus.domain.PhMerchant;
import cn.offway.zeus.dto.MerchantDto;

/**
 * 商户表Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhMerchantService{

	PhMerchant save(PhMerchant phMerchant);
	
	PhMerchant findById(Long id);

	double calculateFare(Long id, int num, Long addrId);

	Page<PhMerchant> findByPage(MerchantDto merchantDto, Pageable page);
}
