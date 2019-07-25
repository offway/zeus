package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhMerchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhInviteRecord;
import cn.offway.zeus.repository.PhInviteRecordRepository;
import cn.offway.zeus.service.PhInviteRecordService;


/**
 * 邀请记录表Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
@Service
public class PhInviteRecordServiceImpl implements PhInviteRecordService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhInviteRecordRepository phInviteRecordRepository;
	
	@Override
	public PhInviteRecord save(PhInviteRecord phInviteRecord){
		return phInviteRecordRepository.save(phInviteRecord);
	}
	
	@Override
	public PhInviteRecord findById(Long id){
		Optional<PhInviteRecord> optional = phInviteRecordRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public List<PhInviteRecord> findByProductIdAndUnionid(Long productId, String unionid){
		return phInviteRecordRepository.findByProductIdAndUnionid(productId, unionid);
	}
	
	@Override
	public int countByProductIdAndUnionid(Long productId, String unionid){
		return phInviteRecordRepository.countByProductIdAndUnionid(productId, unionid);
	}
}
