package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhSettlementDetailService;

import cn.offway.zeus.domain.PhSettlementDetail;
import cn.offway.zeus.repository.PhSettlementDetailRepository;

import java.util.List;


/**
 * 商户结算明细表Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhSettlementDetailServiceImpl implements PhSettlementDetailService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhSettlementDetailRepository phSettlementDetailRepository;
	
	@Override
	public PhSettlementDetail save(PhSettlementDetail phSettlementDetail){
		return phSettlementDetailRepository.save(phSettlementDetail);
	}

	@Override
	public List<PhSettlementDetail> save(List<PhSettlementDetail> phSettlementDetails){
		return phSettlementDetailRepository.saveAll(phSettlementDetails);
	}
	
	@Override
	public PhSettlementDetail getOne(Long id){
		return phSettlementDetailRepository.getOne(id);
	}
}
