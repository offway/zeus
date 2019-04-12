package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhVoucherInfo;

/**
 * 优惠券Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhVoucherInfoService{

	PhVoucherInfo save(PhVoucherInfo phVoucherInfo);
	
	PhVoucherInfo findOne(Long id);

	List<PhVoucherInfo> list(Long userId, Long goodsId, Long brandId);

	List<PhVoucherInfo> findByUserIdOrderByCreateTimeDesc(Long userId);
}
