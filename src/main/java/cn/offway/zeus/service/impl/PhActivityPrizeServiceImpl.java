package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.offway.zeus.service.PhActivityInfoService;
import cn.offway.zeus.service.PhActivityJoinService;
import cn.offway.zeus.service.PhActivityPrizeService;
import cn.offway.zeus.domain.PhActivityInfo;
import cn.offway.zeus.domain.PhActivityJoin;
import cn.offway.zeus.domain.PhActivityPrize;
import cn.offway.zeus.repository.PhActivityPrizeRepository;


/**
 * 活动奖品表-每日福利Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
@Service
public class PhActivityPrizeServiceImpl implements PhActivityPrizeService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhActivityPrizeRepository phActivityPrizeRepository;
	
	@Autowired
	private PhActivityJoinService phActivityJoinService;
	
	@Autowired
	private PhActivityInfoService phActivityInfoService;
	
	@Override
	public PhActivityPrize save(PhActivityPrize phActivityPrize){
		return phActivityPrizeRepository.save(phActivityPrize);
	}
	
	@Override
	public PhActivityPrize getOne(Long id){
		return phActivityPrizeRepository.getOne(id);
	}
	
	@Override
	public PhActivityPrize findByActivityIdAndUnionid(Long activityid,String unionid){
		return phActivityPrizeRepository.findByActivityIdAndUnionid(activityid, unionid);
	}
	
	@Override
	public List<PhActivityPrize> findByActivityId(Long activityid){
		return phActivityPrizeRepository.findByActivityId(activityid);
	}
	
	@Override
	public void open(Long activityId){
		/**
		 *  
		 * 1、查询参与用户
		 * 2、过滤黑名单
		 * 3、过滤已中奖用户
		 * 4、随机抽取
		 * 5、保存中奖记录
		 */
		
		PhActivityInfo phActivityInfo = phActivityInfoService.getOne(activityId);
		
		//获得中奖用户
		List<PhActivityJoin> activityJoins = phActivityJoinService.luckly(phActivityInfo.getId(),phActivityInfo.getWinNum());
		
		Date now = new Date();
		List<PhActivityPrize> phActivityPrizes = new ArrayList<>();
		List<Long> ids = new ArrayList<>();
		for (PhActivityJoin phActivityJoin : activityJoins) {
			PhActivityPrize phActivityPrize = new PhActivityPrize();
			phActivityPrize.setActivityId(phActivityInfo.getId());
			phActivityPrize.setActivityImage(phActivityInfo.getImage());
			phActivityPrize.setActivityName(phActivityInfo.getName());
			phActivityPrize.setCreateTime(now);
			phActivityPrize.setHeadUrl(phActivityJoin.getHeadUrl());
			phActivityPrize.setNickName(phActivityJoin.getNickName());
			phActivityPrize.setStatus("0");
			phActivityPrize.setUnionid(phActivityJoin.getUnionid());
			
			ids.add(phActivityJoin.getId());
			phActivityPrizes.add(phActivityPrize);
		}
		
		phActivityJoinService.updateLuckly(ids);
		phActivityPrizeRepository.saveAll(phActivityPrizes);
		
		
	}
	
}
