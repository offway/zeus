package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhSettlementDetail;

import java.util.List;

/**
 * 商户结算明细表Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhSettlementDetailService{

	PhSettlementDetail save(PhSettlementDetail phSettlementDetail);
	
	PhSettlementDetail getOne(Long id);

	List<PhSettlementDetail> save(List<PhSettlementDetail> phSettlementDetails);
}
