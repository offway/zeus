package cn.offway.zeus.controller;

import java.text.SimpleDateFormat;
import java.util.*;

import cn.offway.zeus.domain.PhFreeProduct;
import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.repository.PhFreeProductRepository;
import cn.offway.zeus.service.PhUserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhFreeDelivery;
import cn.offway.zeus.domain.PhFreeDeliveryUser;
import cn.offway.zeus.dto.PhFreeDeliveryDto;
import cn.offway.zeus.repository.PhFreeDeliveryBoostRepository;
import cn.offway.zeus.service.PhFreeDeliveryService;
import cn.offway.zeus.service.PhFreeDeliveryUserService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags={"免费送"})
@RestController
@RequestMapping("/freeDelivery")
public class FreeDeliveryController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhFreeDeliveryService phFreeDeliveryService;
	
	@Autowired
	private PhFreeDeliveryUserService phFreeDeliveryUserService;
	
	@Autowired
	private PhFreeDeliveryBoostRepository phFreeDeliveryBoostRepository;

	@Autowired
	private PhFreeProductRepository phFreeProductRepository;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	private static final String SMS_CODE_KEY="zeus.sms.code";

	@Autowired
	private PhUserInfoService phUserInfoService;
	
	@ApiOperation("列表")
	@GetMapping("/list")
	public JsonResult list(
			@ApiParam("用户ID") @RequestParam(required = false) Long userId,
			@ApiParam("页码,从0开始") @RequestParam int page,
			@ApiParam("页大小") @RequestParam int size,
			@ApiParam("免费送产品ID") @RequestParam Long productId){
		Page<PhFreeDelivery> pages = phFreeDeliveryService.findByPage(productId, PageRequest.of(page, size));
		List<PhFreeDelivery> phFreeDeliveries = pages.getContent();
		List<PhFreeDeliveryDto> dtos = new ArrayList<>();
		for (PhFreeDelivery phFreeDelivery : phFreeDeliveries) {
			
			PhFreeDeliveryDto dto = new PhFreeDeliveryDto();
			if(null!=phFreeDelivery){
				BeanUtils.copyProperties(phFreeDelivery, dto);
			}
			Long freedeliveryId = phFreeDelivery.getId();
			PhFreeDeliveryUser phFreeDeliveryUser = phFreeDeliveryUserService.findByFreeDeliveryIdAndUserId(freedeliveryId, userId);
			Long currentCount = 0L;
			if(null != phFreeDeliveryUser){
				currentCount = phFreeDeliveryUser.getCurrentCount();
			}
			dto.setCurrentCount(currentCount);
			List<Map<String, Object>> maps = new ArrayList<>();
			List<PhFreeDeliveryUser> phFreeDeliveryUsers = phFreeDeliveryUserService.findByFreeDeliveryId(freedeliveryId);
			for (PhFreeDeliveryUser fdu : phFreeDeliveryUsers) {
				Map<String, Object> map = new HashMap<>();
				map.put("headimgurl", fdu.getHeadimgurl());
				map.put("currentCount", fdu.getCurrentCount());
				maps.add(map);
			}
			dto.setBoosts(maps);
			dtos.add(dto);
		}
		return jsonResultHelper.buildSuccessJsonResult(new PageImpl<>(dtos, PageRequest.of(page,size), pages.getTotalElements()));
	}
	
	@ApiOperation("详情")
	@GetMapping("/info")
	public JsonResult info(
			@ApiParam("免费送ID") @RequestParam Long freeDeliveryId,
			@ApiParam("用户ID") @RequestParam(required = false) Long userId){
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("freeDelivery", phFreeDeliveryService.findById(freeDeliveryId));
		resultMap.put("ranking", phFreeDeliveryUserService.ranking(freeDeliveryId));
		PhFreeDeliveryUser phFreeDeliveryUser = phFreeDeliveryUserService.findByFreeDeliveryIdAndUserId(freeDeliveryId, userId);
		if(null!=phFreeDeliveryUser){
			resultMap.put("boosts", phFreeDeliveryBoostRepository.findByFreeDeliveryUserIdOrderByCreateTimeDesc(phFreeDeliveryUser.getId()));
		}

		return jsonResultHelper.buildSuccessJsonResult(resultMap);
	}

	@ApiOperation("免费送产品详情")
	@GetMapping("/product")
	public JsonResult product(@ApiParam("免费送产品ID") @RequestParam Long productId){
		Optional<PhFreeProduct> optional = phFreeProductRepository.findById(productId);
		return jsonResultHelper.buildSuccessJsonResult(optional.isPresent()?optional.get():null);
	}
	
	@ApiOperation("助力")
	@PostMapping("/boost")
	public JsonResult boost(
			@ApiParam("免费送ID") @RequestParam Long freeDeliveryId,
			@ApiParam("分享用户ID") @RequestParam Long userId,
			@ApiParam("助力用户手机号") @RequestParam String phone,
			@ApiParam("助力用户验证码") @RequestParam String code){
		
		try {

			if(StringUtils.isBlank(phone)){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_MISS);
			}

			phone = phone.contains("+")?phone:"+86"+phone;

			String smsCode = stringRedisTemplate.opsForValue().get(SMS_CODE_KEY+"_"+phone);
			if(StringUtils.isBlank(smsCode)){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_INVALID);
			}

			if(!code.equals(smsCode)){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_ERROR);
			}

			PhFreeDelivery phFreeDelivery = phFreeDeliveryService.findById(freeDeliveryId);
			if("1".equals(phFreeDelivery.getStatus())){
				//已抢光
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.FREE_LESS);
			}

			PhUserInfo phUserInfo = phUserInfoService.findByPhone(phone);

			//用户类型[0-新用户,1-老用户]
			String userType = phFreeDelivery.getUserType();
			if("0".equals(userType)){
				if(null!=phUserInfo){
					return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_EXISTS);
				}
			}

			if(null==phUserInfo){
				phUserInfo = phUserInfoService.register(phone,null,null,null,null,null,null,null,null);
			}

			Long boostUserId = phUserInfo.getId();

			if(userId.longValue()==boostUserId.longValue()){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.FREE_BOOST_MY);

			}

			Optional<PhFreeProduct> optional = phFreeProductRepository.findById(phFreeDelivery.getProductId());
			PhFreeProduct phFreeProduct =  optional.isPresent()?optional.get():null;
			Date beginTime = phFreeProduct.getBeginTime();
			Date endTime = phFreeProduct.getEndTime();
			Date now = new Date();
			if(now.before(beginTime) || now.after(endTime)){
				//不在活动时间范围
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_END);
			}


			return phFreeDeliveryService.boost(freeDeliveryId,userId,boostUserId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("免费送活动助理失败freeDeliveryId={},userId={},phone={}",freeDeliveryId,userId,phone,e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
}
