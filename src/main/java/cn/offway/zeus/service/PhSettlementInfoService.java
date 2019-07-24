package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhSettlementInfo;

/**
 * 商户结算表Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhSettlementInfoService{

	PhSettlementInfo save(PhSettlementInfo phSettlementInfo);
	
	PhSettlementInfo getOne(Long id);

    void save(String preorderNo);
}
