package cn.offway.zeus.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import cn.offway.zeus.domain.*;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.exception.StockException;
import cn.offway.zeus.repository.PhLaborLuckyRepository;
import cn.offway.zeus.repository.PhLaborPrizeRepository;
import cn.offway.zeus.repository.PhLaborRepository;
import cn.offway.zeus.service.PhConfigService;
import cn.offway.zeus.service.PhLaborService;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.service.PhVoucherInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;


/**
 * 劳动节活动Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhLaborServiceImpl implements PhLaborService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhLaborRepository phLaborRepository;
	
	@Autowired
	private PhLaborPrizeRepository phLaborPrizeRepository;
	
	@Autowired
	private PhLaborLuckyRepository phLaborLuckyRepository;
	
	@Autowired
	private PhVoucherInfoService phVoucherInfoService;
	
	@Autowired
	private PhConfigService phConfigService;
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhUserInfoService phUserInfoService;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	private static final String LABOR_LOTTERY_KEY="zeus.labor.lottery";
	private static final String LABOR_SHARE_KEY="zeus.labor.share";
	
	
	@Override
	public PhLabor save(PhLabor phLabor){
		return phLaborRepository.save(phLabor);
	}
	
	@Override
	public PhLabor findById(Long id){
		Optional<PhLabor> optional = phLaborRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	@Override
	public PhLabor findByUserId(Long userId){
		return phLaborRepository.findByUserId(userId);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {Exception.class,StockException.class})
	public JsonResult lottery(Long userId) throws Exception{
		
		Map<String, Object> resultMap = new HashMap<>();
		int c = phLaborRepository.subLotteryNum(userId);
		if(c != 1){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.LOTTERYNUM_LESS);
		}
		
		PhUserInfo phUserInfo = phUserInfoService.findById(userId);
		
		
		Date now = new Date();
		int random = RandomUtils.nextInt(1, 1000);
		
		//查询OFFWAY惊喜礼包是否已经领取完
		PhLaborPrize phLaborPrize = phLaborPrizeRepository.lottery("0");
		
		String name = "";
		int index = 1;
		if(null != phLaborPrize){
			if(random <= 25){
				//OFFWAY惊喜礼包
				phLaborPrize.setStatus("1");
				phLaborPrizeRepository.save(phLaborPrize);
				
				name = phLaborPrize.getName();
				PhLaborLucky phLaborLucky = new PhLaborLucky();
				phLaborLucky.setCreateTime(now);
				phLaborLucky.setName(name);
				phLaborLucky.setUserId(userId);
				phLaborLucky.setNickname(phUserInfo.getNickname());
				phLaborLucky.setHeadimgurl(phUserInfo.getHeadimgurl());
				phLaborLucky.setVersion(0L);
				phLaborLuckyRepository.save(phLaborLucky);
				index = 1;
				
			}else if(random >=26 && random <= 50){
				//10元无门槛优惠券
				name = voucher(phUserInfo,"VP_AYO_10");
				index = 2;
			}else{
				int rd = RandomUtils.nextInt(1, 100);
				if (rd <=40){
					name = voucher(phUserInfo,"VP_AYO_30");
				}else if(rd>=41 && rd<=70){
					name = voucher(phUserInfo,"VP_AYO_50");
				}else{
					name = voucher(phUserInfo,"VP_AYO_80");
				}
				index = 3;
			}
		}else{
			int rd = RandomUtils.nextInt(1, 1000);
			if (rd <=425){
				name = voucher(phUserInfo,"VP_AYO_30");
			}else if(rd>=426 && rd<=725){
				name = voucher(phUserInfo,"VP_AYO_50");
			}else{
				name = voucher(phUserInfo,"VP_AYO_80");
			}
			index = 3;
		}
		
		resultMap.put("index", index);
		resultMap.put("name", name);
		
		return jsonResultHelper.buildSuccessJsonResult(resultMap);
	}

	private String voucher(PhUserInfo phUserInfo,String configName) throws Exception {
		Long userId = phUserInfo.getId();
		PhConfig config = phConfigService.findByName(configName);
		boolean result = phVoucherInfoService.giveVoucher(userId, Long.parseLong(config.getContent()));
		if(!result){
			throw new Exception("发放加息券异常");
		}
		String name = config.getRemark();
		PhLaborLucky phLaborLucky = new PhLaborLucky();
		phLaborLucky.setCreateTime(new Date());
		phLaborLucky.setName(name);
		phLaborLucky.setUserId(userId);
		phLaborLucky.setVersion(0L);
		phLaborLucky.setNickname(phUserInfo.getNickname());
		phLaborLucky.setHeadimgurl(phUserInfo.getHeadimgurl());
		phLaborLuckyRepository.save(phLaborLucky);
		return name;
	}

	private String voucherlist(Long userId, Date now,PhUserInfo phUserInfo) throws Exception {
		PhLaborPrize  laborPrize = phLaborPrizeRepository.lottery("1");
		String name = laborPrize.getName();
		PhLaborLucky phLaborLucky = new PhLaborLucky();
		phLaborLucky.setCreateTime(now);
		phLaborLucky.setName(name);
		phLaborLucky.setUserId(userId);
		phLaborLucky.setVersion(0L);
		phLaborLucky.setNickname(phUserInfo.getNickname());
		phLaborLucky.setHeadimgurl(phUserInfo.getHeadimgurl());
		phLaborLuckyRepository.save(phLaborLucky);
		
		Long voucherProjectId = laborPrize.getVoucherProjectId();
		boolean result = phVoucherInfoService.giveVoucher(userId, voucherProjectId);
		if(!result){
			throw new Exception("发放加息券异常");
		}
		return name;
	}

	@Override
	public Map<String,Object> init(Long userId){
		Map<String,Object> map = new HashMap<>();
		Long lotteryNum = 0L;
		Long shareNum = 2L;
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String lotteryNumRedis = stringRedisTemplate.opsForValue().get(LABOR_LOTTERY_KEY+"."+userId+"."+sdf.format(now));
		PhLabor phLabor = phLaborRepository.findByUserId(userId);
		if(StringUtils.isBlank(lotteryNumRedis)){
			//每天抽奖次数加1
			if(null == phLabor){
				phLabor = new PhLabor();
				phLabor.setCreateTime(now);
				phLabor.setLotteryNum(1L);
				phLabor.setUserId(userId);
				phLabor.setVersion(0L);
				phLabor = save(phLabor);
			}else{
				phLabor.setLotteryNum(phLabor.getLotteryNum().longValue()+1L);
				phLabor = save(phLabor);
			}
			stringRedisTemplate.opsForValue().set(LABOR_LOTTERY_KEY+"."+userId+"."+sdf.format(now),""+lotteryNum,1, TimeUnit.DAYS);
		}

		lotteryNum = phLabor.getLotteryNum();

		String shareNumRedis = stringRedisTemplate.opsForValue().get(LABOR_SHARE_KEY+"."+userId+"."+sdf.format(now));
		if(StringUtils.isNotBlank(shareNumRedis)){
			shareNum -=Integer.parseInt(shareNumRedis);
		}

		map.put("lotteryNum",lotteryNum);
		map.put("shareNum",shareNum);
		map.put("winningRecord",phLaborLuckyRepository.findByUserIdOrderByCreateTimeDesc(userId));
		return map;
	}

	@Override
	public Long addshareNum(Long userId) {
		int shareNum = 0;
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String shareNumRedis = stringRedisTemplate.opsForValue().get(LABOR_SHARE_KEY+"."+userId+"."+sdf.format(now));
		if(StringUtils.isNotBlank(shareNumRedis)){
			shareNum = Integer.parseInt(shareNumRedis);
		}
		PhLabor phLabor = phLaborRepository.findByUserId(userId);
		if(shareNum < 2){
			stringRedisTemplate.opsForValue().set(LABOR_SHARE_KEY+"."+userId+"."+sdf.format(now),""+(shareNum+1),1, TimeUnit.DAYS);
			if(null == phLabor){
				phLabor = new PhLabor();
				phLabor.setCreateTime(now);
				phLabor.setLotteryNum(1L);
				phLabor.setUserId(userId);
				phLabor.setVersion(0L);
				phLabor = save(phLabor);
			}else{
				phLabor.setLotteryNum(phLabor.getLotteryNum().longValue()+1L);
				phLabor = save(phLabor);
			}
		}
		return phLabor.getLotteryNum();
	}

	
	@Override
	public boolean sign(Long userId){
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int count = phLaborRepository.countByUserIdAndSignDate(userId, sdf.format(now));
		if(count == 0){
			PhLabor phLabor = phLaborRepository.findByUserId(userId);
			if(null == phLabor){
				phLabor = new PhLabor();
				phLabor.setCreateTime(now);
				phLabor.setLotteryNum(3L);
				phLabor.setSignDate(sdf.format(now));
				phLabor.setSignNum(1L);
				phLabor.setUserId(userId);
				phLabor.setVersion(0L);
				save(phLabor);
			}else{
				long signNum = phLabor.getSignNum().longValue()+1L;
				phLabor.setSignNum(signNum);
				long lotteryNum = phLabor.getLotteryNum().longValue();
				if(signNum < 6L){
					lotteryNum += 3L;
				}else if(signNum == 6L){
					lotteryNum += 4L;
				}else if(signNum == 7L){
					lotteryNum += 5L;
				}
				phLabor.setLotteryNum(lotteryNum);
				phLabor.setSignDate(sdf.format(now));
				save(phLabor);
			}
			return true;
		}
		return false;
	}
}
