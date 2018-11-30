package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhActivityImage;
import cn.offway.zeus.domain.PhActivityInfo;
import cn.offway.zeus.domain.PhActivityPrize;
import cn.offway.zeus.dto.ActivityInfo;
import cn.offway.zeus.dto.ActivityJoin;
import cn.offway.zeus.repository.PhActivityInfoRepository;
import cn.offway.zeus.service.PhActivityImageService;
import cn.offway.zeus.service.PhActivityInfoService;
import cn.offway.zeus.service.PhActivityJoinService;
import cn.offway.zeus.service.PhActivityPrizeService;


/**
 * 活动表-每日福利Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
@Service
public class PhActivityInfoServiceImpl implements PhActivityInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhActivityInfoRepository phActivityInfoRepository;
	
	@Autowired
	private PhActivityImageService phActivityImageService;
	
	@Autowired
	private PhActivityJoinService phActivityJoinService;
	
	@Autowired
	private PhActivityPrizeService phActivityPrizeService;
	
	@Override
	public PhActivityInfo save(PhActivityInfo phActivityInfo){
		return phActivityInfoRepository.save(phActivityInfo);
	}
	
	@Override
	public PhActivityInfo findOne(Long id){
		return phActivityInfoRepository.findOne(id);
	}
	
	@Override
	public Map<String, List<PhActivityInfo>> list(){
		Map<String, List<PhActivityInfo>> resultMap = new HashMap<>();
		resultMap.put("current", phActivityInfoRepository.findByNow());
		resultMap.put("before", phActivityInfoRepository.findByBefore());
		return resultMap;
	}
	
	@Override
	public Map<String, Object> detail(Long activityId,String unionid){
		Map<String, Object> resultMap = new HashMap<>();
		PhActivityInfo phActivityInfo = findOne(activityId);
		
		ActivityInfo activityInfo = new ActivityInfo();
		BeanUtils.copyProperties(phActivityInfo, activityInfo);
		
		Date now = new Date();
		if(now.before(activityInfo.getBeginTime())){
			activityInfo.setStatus("0");
		}else if(now.after(activityInfo.getEndTime()) || now.equals(activityInfo.getEndTime())){
			if(now.after(DateUtils.addDays(activityInfo.getEndTime(), 7))){
				//已过期
				activityInfo.setStatus("3");
			}else{
				activityInfo.setStatus("2");
			}
		}else{
			activityInfo.setStatus("1");
		}
		
		resultMap.put("activity", activityInfo);
		//查询活动图片
		List<PhActivityImage> images = phActivityImageService.findByActivityId(activityId);
		Map<String, List<String>> img = new HashMap<>();
		for (PhActivityImage phActivityImage : images) {
			String key = phActivityImage.getType();
			key = "0".equals(key)?"banner":"detail";
			List<String> urls = img.get(key);
			if(null == urls ){
				urls = new ArrayList<>();
			}
			urls.add(phActivityImage.getImageUrl());
			img.put(key,urls);
		}
		resultMap.putAll(img);
		
		int count = phActivityJoinService.countByUnionidAndActivityId(unionid, activityId);
		resultMap.put("isJoin", count>0?true:false);
		
		boolean isWin = false;
		boolean isAddr = false;
		List<Map<String, Object>> wins = new ArrayList<>(); 
		List<PhActivityPrize> phActivityPrizes = phActivityPrizeService.findByActivityId(activityId);
		for (PhActivityPrize phActivityPrize : phActivityPrizes) {
			if(phActivityPrize.getUnionid().equals(unionid)){
				isWin = true;
				if(StringUtils.isNotBlank(phActivityPrize.getAddr())){
					isAddr = true;
				}
			}
			Map<String, Object> map = new HashMap<>();
			map.put("headUrl", phActivityPrize.getHeadUrl());
			map.put("nickName", phActivityPrize.getNickName());
			wins.add(map);
		}
		
		resultMap.put("isWin", isWin);
		resultMap.put("isAddr", isAddr);
		resultMap.put("wins", wins);
		
		
		return resultMap;
	}
	
}
