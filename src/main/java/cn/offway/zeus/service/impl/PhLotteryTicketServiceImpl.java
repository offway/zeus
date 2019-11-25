package cn.offway.zeus.service.impl;

import cn.offway.zeus.domain.PhInviteRecord;
import cn.offway.zeus.domain.PhLotteryTicket;
import cn.offway.zeus.domain.PhProductInfo;
import cn.offway.zeus.domain.PhWxuserInfo;
import cn.offway.zeus.enums.TicketSourceEnum;
import cn.offway.zeus.repository.PhLotteryTicketRepository;
import cn.offway.zeus.service.PhInviteRecordService;
import cn.offway.zeus.service.PhLotteryTicketService;
import cn.offway.zeus.service.PhProductInfoService;
import cn.offway.zeus.service.PhWxuserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * 抽奖券表Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
@Service
public class PhLotteryTicketServiceImpl implements PhLotteryTicketService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhLotteryTicketRepository phLotteryTicketRepository;
	
	@Autowired
	private PhWxuserInfoService phWxuserInfoService;
	
	@Autowired
	private PhInviteRecordService phInviteRecordService;
	
	@Autowired
	private PhProductInfoService phProductInfoService;
	
	@Override
	public PhLotteryTicket save(PhLotteryTicket phLotteryTicket){
		return phLotteryTicketRepository.save(phLotteryTicket);
	}
	
	@Override
	public PhLotteryTicket findById(Long id){
		Optional<PhLotteryTicket> optional = phLotteryTicketRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public int countByProductIdAndUnionidAndSource(Long productId,String unionid,String source){
		return phLotteryTicketRepository.countByProductIdAndUnionidAndSource(productId, unionid, source);
	}
	
	@Override
	public int countByUnionidAndSource(String unionid,String source){
		return phLotteryTicketRepository.countByUnionidAndSource(unionid, source);
	}
	
	@Override
	public int updateFormId(Long productId,String unionid,String formId){
		return phLotteryTicketRepository.updateFormId(productId, unionid, formId);
	}
	
	@Override
	public List<PhLotteryTicket> findByProductIdAndUnionid(Long productId,String unionid){
		return phLotteryTicketRepository.findByProductIdAndUnionid(productId,unionid);
	}

	@Override
	public void register(Long productId, PhWxuserInfo phWxuserInfo ,PhWxuserInfo iphWxuserInfo,String formId) {
		List<PhLotteryTicket> phLotteryTickets = new ArrayList<>();
		PhLotteryTicket phLotteryTicket = new PhLotteryTicket();
		phLotteryTicket.setCreateTime(new Date());
		phLotteryTicket.setHeadUrl(phWxuserInfo.getHeadimgurl());
		phLotteryTicket.setNickName(phWxuserInfo.getNickname());
		phLotteryTicket.setUnionid(phWxuserInfo.getUnionid());
		phLotteryTicket.setProductId(productId);
		phLotteryTicket.setSource(TicketSourceEnum.JOIN.getCode());
		phLotteryTicket.setRemark("登记抽奖获得");
		phLotteryTicket.setFormId(formId);
		phLotteryTickets.add(phLotteryTicket);
		
		
		PhProductInfo phProductInfo = phProductInfoService.findById(productId);
		String channel = StringUtils.isNotBlank(formId)?"小程序":"公众号";
//		saTrack(phWxuserInfo.getUnionid(), phLotteryTickets.size(),phProductInfo.getName(),channel);

		
		if(null != iphWxuserInfo){
			//邀请奖励
			inviteExcute(phProductInfo, phWxuserInfo, iphWxuserInfo, phLotteryTickets,channel);
		}
		
		
		phLotteryTicketRepository.saveAll(phLotteryTickets);
		//更新抽奖码
		phLotteryTicketRepository.updateCode();
	}

	private void inviteExcute(PhProductInfo phProductInfo, PhWxuserInfo phWxuserInfo, PhWxuserInfo iphWxuserInfo,
			List<PhLotteryTicket> phLotteryTickets,String channel) {
		
		//查询邀请用户数
		int inviteCount = phInviteRecordService.countByProductIdAndUnionid(phProductInfo.getId(), iphWxuserInfo.getUnionid());
		//邀请50人以上不发放抽奖券
		if(inviteCount < 50){
			
			//邀请人获得抽奖码
			inviteCount ++; 
			//邀请人获得抽奖码数
			int getTickets = 1;
			switch (inviteCount) {
			case 2:
				getTickets = 2;
				break;
			case 4:
				getTickets = 3;
				break;
			case 8:
				getTickets = 4;
				break;
			case 15:
				getTickets = 6;
				break;
			case 30:
				getTickets = 10;
				break;
			default:
				getTickets = 1;
				break;
			}
			
			for (int i = 0; i < getTickets; i++) {
				PhLotteryTicket phLotteryTicket = new PhLotteryTicket();
				phLotteryTicket.setCreateTime(new Date());
				phLotteryTicket.setHeadUrl(iphWxuserInfo.getHeadimgurl());
				phLotteryTicket.setNickName(iphWxuserInfo.getNickname());
				phLotteryTicket.setUnionid(iphWxuserInfo.getUnionid());
				phLotteryTicket.setProductId(phProductInfo.getId());
				phLotteryTicket.setSource(TicketSourceEnum.INVITE.getCode());
				phLotteryTicket.setRemark("邀请用户所得,邀请了用户unionid:"+phWxuserInfo.getUnionid());
				phLotteryTickets.add(phLotteryTicket);
			}
		}
		
		
		//保存邀请记录
		PhInviteRecord phInviteRecord = new PhInviteRecord();
		phInviteRecord.setCreateTime(new Date());
		phInviteRecord.setInviteUnionid(phWxuserInfo.getUnionid());//被邀请用户
		phInviteRecord.setInviteHeadUrl(phWxuserInfo.getHeadimgurl());
		phInviteRecord.setInviteNickName(phWxuserInfo.getNickname());
		phInviteRecord.setUnionid(iphWxuserInfo.getUnionid());
		phInviteRecord.setProductId(phProductInfo.getId());
		phInviteRecordService.save(phInviteRecord);
		
	}
	
	@Override
	public void shareTicket(String unionid,Long productId,String channel){
		//仅第一次分享获得抽奖码
		int count = countByProductIdAndUnionidAndSource(productId, unionid, TicketSourceEnum.SHARE.getCode());
		if(count == 0){
			List<PhLotteryTicket> phLotteryTickets = new ArrayList<>();
			PhWxuserInfo phWxuserInfo = phWxuserInfoService.findByUnionid(unionid);
			for (int i = 0; i < 2; i++) {
				PhLotteryTicket phLotteryTicket = new PhLotteryTicket();
				phLotteryTicket.setCreateTime(new Date());
				phLotteryTicket.setHeadUrl(phWxuserInfo.getHeadimgurl());
				phLotteryTicket.setNickName(phWxuserInfo.getNickname());
				phLotteryTicket.setUnionid(phWxuserInfo.getUnionid());
				phLotteryTicket.setProductId(productId);
				phLotteryTicket.setSource(TicketSourceEnum.SHARE.getCode());
				phLotteryTicket.setRemark("分享获得");
				phLotteryTickets.add(phLotteryTicket);
			}
			
			phLotteryTicketRepository.saveAll(phLotteryTickets);
			//更新抽奖码
			phLotteryTicketRepository.updateCode();
			
			PhProductInfo phProductInfo = phProductInfoService.findById(productId);
		}
	}
	

	@Override
	public void appTicket(String unionid) throws Exception{
		PhWxuserInfo phWxuserInfo = phWxuserInfoService.findByUnionid(unionid);
		if(null != phWxuserInfo){
			//检查是否已经赠送
			int count = phLotteryTicketRepository.countByUnionidAndSource(unionid,TicketSourceEnum.APP_REGISTER.getCode());
			if(count == 0){
				//查询用户参与的正在进行的活动
				List<Object> productIds = phLotteryTicketRepository.findProductId(unionid);
				List<PhLotteryTicket> phLotteryTickets = new ArrayList<>();
				
				for (Object productId : productIds) {
					for (int i = 0; i < 5; i++) {
						PhLotteryTicket phLotteryTicket = new PhLotteryTicket();
						phLotteryTicket.setCreateTime(new Date());
						phLotteryTicket.setHeadUrl(phWxuserInfo.getHeadimgurl());
						phLotteryTicket.setNickName(phWxuserInfo.getNickname());
						phLotteryTicket.setUnionid(phWxuserInfo.getUnionid());
						phLotteryTicket.setProductId(Long.parseLong(productId.toString()));
						phLotteryTicket.setSource(TicketSourceEnum.APP_REGISTER.getCode());
						phLotteryTicket.setRemark("APP注册");
						phLotteryTickets.add(phLotteryTicket);
					}
				}

				phLotteryTicketRepository.saveAll(phLotteryTickets);
				//更新抽奖码
				phLotteryTicketRepository.updateCode();
				
			}
		}
		
		
	}
}
