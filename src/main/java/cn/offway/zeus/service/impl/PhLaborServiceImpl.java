package cn.offway.zeus.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.service.PhConfigService;
import cn.offway.zeus.service.PhLaborService;
import cn.offway.zeus.service.PhVoucherInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import cn.offway.zeus.domain.PhLabor;
import cn.offway.zeus.domain.PhLaborLucky;
import cn.offway.zeus.domain.PhLaborPrize;
import cn.offway.zeus.exception.StockException;
import cn.offway.zeus.repository.PhLaborLuckyRepository;
import cn.offway.zeus.repository.PhLaborPrizeRepository;
import cn.offway.zeus.repository.PhLaborRepository;


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
	
	
	@Override
	public PhLabor save(PhLabor phLabor){
		return phLaborRepository.save(phLabor);
	}
	
	@Override
	public PhLabor findOne(Long id){
		return phLaborRepository.findOne(id);
	}
	@Override
	public PhLabor findByUserId(Long userId){
		return phLaborRepository.findByUserId(userId);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {Exception.class,StockException.class})
	public JsonResult lottery(Long userId) throws Exception{
		
		int c = phLaborRepository.subLotteryNum(userId);
		if(c != 1){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.LOTTERYNUM_LESS);
		}
		Date now = new Date();
		int random = RandomUtils.nextInt(1, 100);
		
		//查询OFFWAY惊喜礼包是否已经领取完
		PhLaborPrize phLaborPrize = phLaborPrizeRepository.lottery("0");
		
		String name = "";
		if(null != phLaborPrize){
			if(random <= 5){
				//OFFWAY惊喜礼包
				phLaborPrize.setStatus("1");
				phLaborPrizeRepository.save(phLaborPrize);
				
				name = phLaborPrize.getName();
				PhLaborLucky phLaborLucky = new PhLaborLucky();
				phLaborLucky.setCreateTime(now);
				phLaborLucky.setName(name);
				phLaborLucky.setUserId(userId);
				phLaborLucky.setVersion(0L);
				phLaborLuckyRepository.save(phLaborLucky);
				
			}else if(random >=6 && random <= 70){
				//5-200元现金礼包
				name = voucherlist(userId, now);
			}else{
				//5元无门槛优惠券
				name = voucher(userId, now);
			}
		}else{
			if(random <= 70){
				//5-200元现金礼包
				name = voucherlist(userId, now);
			}else{
				//5元无门槛优惠券
				name = voucher(userId, now);
			}
		}
		
		return jsonResultHelper.buildSuccessJsonResult(name);
	}

	private String voucher(Long userId, Date now) throws Exception {
		String content = phConfigService.findContentByName("VP_PLATFORM_5");
		boolean result = phVoucherInfoService.giveVoucher(userId, Long.parseLong(content));
		if(!result){
			throw new Exception("发放加息券异常");
		}
		String name = "5元无门槛优惠券";
		PhLaborLucky phLaborLucky = new PhLaborLucky();
		phLaborLucky.setCreateTime(now);
		phLaborLucky.setName(name);
		phLaborLucky.setUserId(userId);
		phLaborLucky.setVersion(0L);
		phLaborLuckyRepository.save(phLaborLucky);
		return "5元无门槛优惠券";
	}

	private String voucherlist(Long userId, Date now) throws Exception {
		PhLaborPrize  laborPrize = phLaborPrizeRepository.lottery("1");
		String name = laborPrize.getName();
		PhLaborLucky phLaborLucky = new PhLaborLucky();
		phLaborLucky.setCreateTime(now);
		phLaborLucky.setName(name);
		phLaborLucky.setUserId(userId);
		phLaborLucky.setVersion(0L);
		phLaborLuckyRepository.save(phLaborLucky);
		
		Long voucherProjectId = laborPrize.getVoucherProjectId();
		boolean result = phVoucherInfoService.giveVoucher(userId, voucherProjectId);
		if(!result){
			throw new Exception("发放加息券异常");
		}
		return name;
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
