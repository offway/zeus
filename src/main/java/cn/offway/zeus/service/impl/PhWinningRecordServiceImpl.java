package cn.offway.zeus.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhWinningRecord;
import cn.offway.zeus.repository.PhWinningRecordRepository;
import cn.offway.zeus.service.PhWinningRecordService;


/**
 * 中奖用户信息Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
@Service
public class PhWinningRecordServiceImpl implements PhWinningRecordService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhWinningRecordRepository phWinningRecordRepository;
	
	@Override
	public PhWinningRecord save(PhWinningRecord phWinningRecord){
		return phWinningRecordRepository.save(phWinningRecord);
	}
	
	@Override
	public PhWinningRecord findOne(Long id){
		return phWinningRecordRepository.findOne(id);
	}
	
	@Override
	public List<PhWinningRecord> findByProductId(Long productId){
		return phWinningRecordRepository.findByProductId(productId);
	}
}
