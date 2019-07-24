package cn.offway.zeus.service.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhShareRecord;
import cn.offway.zeus.domain.PhWxuserInfo;
import cn.offway.zeus.enums.ChannelEnum;
import cn.offway.zeus.repository.PhShareRecordRepository;
import cn.offway.zeus.service.PhLotteryTicketService;
import cn.offway.zeus.service.PhShareRecordService;
import cn.offway.zeus.service.PhWxuserInfoService;


/**
 * 分享记录表Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
@Service
public class PhShareRecordServiceImpl implements PhShareRecordService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhShareRecordRepository phShareRecordRepository;
	
	@Autowired
	private PhWxuserInfoService phWxuserInfoService;
	
	@Autowired
	private PhLotteryTicketService phLotteryTicketService;
	
	@Override
	public PhShareRecord save(PhShareRecord phShareRecord){
		return phShareRecordRepository.save(phShareRecord);
	}
	
	@Override
	public PhShareRecord getOne(Long id){
		return phShareRecordRepository.getOne(id);
	}
	
	@Override
	public int countByUnionidAndProductId(String unionid, Long productId){
		return phShareRecordRepository.countByUnionidAndProductId(unionid, productId);
	}
	
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = Exception.class)
	@Override
	public PhShareRecord saveShare(Long productId, String unionid,String channel) throws Exception{
		
		channel = StringUtils.isBlank(channel)?ChannelEnum.H5.getDesc():channel;

		PhWxuserInfo phWxuserInfo = phWxuserInfoService.findByUnionid(unionid);
		PhShareRecord phShareRecord = new PhShareRecord();
		phShareRecord.setCreateTime(new Date());
		phShareRecord.setHeadUrl(phWxuserInfo.getHeadimgurl());
		phShareRecord.setNickName(phWxuserInfo.getNickname());
		phShareRecord.setUnionid(unionid);
		phShareRecord.setProductId(productId);
		phShareRecord.setChannel(ChannelEnum.getByDesc(channel).getCode());
		
		//抽奖券
		phLotteryTicketService.shareTicket(unionid, productId,channel);
		return save(phShareRecord);
	}
}
