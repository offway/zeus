package cn.offway.zeus.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhVoucherInfoService;

import cn.offway.zeus.domain.PhVoucherInfo;
import cn.offway.zeus.repository.PhVoucherInfoRepository;


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
	public List<PhVoucherInfo> list(Long userId,Long goodsId,Long brandId,Double amount){
		return phVoucherInfoRepository.list(userId, goodsId, brandId,amount);
	}
	
	@Override
	public List<Long> ids(Long userId,Long goodsId,Long brandId,Double amount){
		return phVoucherInfoRepository.ids(userId, goodsId, brandId, amount);
	}
	
	@Override
	public List<PhVoucherInfo> findByUserIdOrderByCreateTimeDesc(Long userId){
		return phVoucherInfoRepository.findByUserIdOrderByCreateTimeDesc(userId);
	}
	
	@Override
	public List<PhVoucherInfo> findByIdInOrderByCreateTimeDesc(List<Long> ids){
		return phVoucherInfoRepository.findByIdInOrderByCreateTimeDesc(ids);
	}
}
