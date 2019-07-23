package cn.offway.zeus.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
	
	@ApiOperation("列表")
	@GetMapping("/list")
	public JsonResult list(
			@ApiParam("用户ID") @RequestParam(required = false) Long userId,
			@ApiParam("页码,从0开始") @RequestParam int page,
			@ApiParam("页大小") @RequestParam int size){
		Page<PhFreeDelivery> pages = phFreeDeliveryService.findByPage(new PageRequest(page, size));
		List<PhFreeDelivery> phFreeDeliveries = pages.getContent();
		List<PhFreeDeliveryDto> dtos = new ArrayList<>();
		for (PhFreeDelivery phFreeDelivery : phFreeDeliveries) {
			
			PhFreeDeliveryDto dto = new PhFreeDeliveryDto();
			BeanUtils.copyProperties(phFreeDelivery, dto);
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
		return jsonResultHelper.buildSuccessJsonResult(new PageImpl<>(dtos, new PageRequest(page,size), pages.getTotalElements()));
	}
	
	@ApiOperation("详情")
	@GetMapping("/info")
	public JsonResult info(
			@ApiParam("免费送ID") @RequestParam Long freeDeliveryId,
			@ApiParam("用户ID") @RequestParam(required = false) Long userId){
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("freeDelivery", phFreeDeliveryService.findOne(freeDeliveryId));
		resultMap.put("ranking", phFreeDeliveryUserService.ranking(freeDeliveryId));
		PhFreeDeliveryUser phFreeDeliveryUser = phFreeDeliveryUserService.findByFreeDeliveryIdAndUserId(freeDeliveryId, userId);
		if(null!=phFreeDeliveryUser){
			resultMap.put("boosts", phFreeDeliveryBoostRepository.findByFreeDeliveryUserIdOrderByCreateTimeDesc(phFreeDeliveryUser.getId()));
		}

		return jsonResultHelper.buildSuccessJsonResult(resultMap);
	}
	
	@ApiOperation("助力")
	@PostMapping("/boost")
	public JsonResult boost(
			@ApiParam("免费送ID") @RequestParam Long freeDeliveryId,
			@ApiParam("分享用户ID") @RequestParam(required = false) Long userId,
			@ApiParam("助力用户ID") @RequestParam(required = false) Long boostUserId){
		
		try {
			
			/*Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(now.before(sdf.parse("2019-06-18 00:00:00")) || now.after(sdf.parse("2019-06-20 23:59:59"))){
				//不在活动时间范围
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_END);
			}*/

			if(userId.longValue()==boostUserId.longValue()){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.FREE_BOOST_MY);

			}
			PhFreeDelivery phFreeDelivery = phFreeDeliveryService.findOne(freeDeliveryId);
			if("1".equals(phFreeDelivery.getStatus())){
				//已抢光
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.FREE_LESS);
			}
			return phFreeDeliveryService.boost(freeDeliveryId,userId,boostUserId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("免费送活动助理失败freeDeliveryId={},userId={},boostUserId={}",freeDeliveryId,userId,boostUserId,e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
}
