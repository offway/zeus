package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhVoucherInfo;
import cn.offway.zeus.dto.VoucherDto;

/**
 * 优惠券Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhVoucherInfoService{

	PhVoucherInfo save(PhVoucherInfo phVoucherInfo);
	
	PhVoucherInfo findOne(Long id);

	List<PhVoucherInfo> findByUserIdOrderByCreateTimeDesc(Long userId);

	List<PhVoucherInfo> findByIdInOrderByCreateTimeDesc(List<Long> ids);
	
	List<PhVoucherInfo> findUseByMerchant(Long userId,Long merchantId,Double amount);
	
	List<PhVoucherInfo> findUseByPlatform(Long userId,Double amount);
	
	int countUseByMerchant(Long userId,Long merchantId,Double amount);
	
	int countUseByPlatform(Long userId,Double amount);

	List<PhVoucherInfo> findAll(VoucherDto voucherDto);

	int updateStatusBym(Long voucherId, Double amount, Long merchant_id, Long userId);

	int updateStatus(Long voucherId, Double amount, Long userId);

	int give(Long userId, List<String> voucherProjectIds);

	int giveByTime(Long userId, List<String> voucherProjectIds);

	boolean giveVoucher(Long userId, Long voucherProjectId);

	void giveVoucher(Long userId, List<String> voucherProjectIds);

}
