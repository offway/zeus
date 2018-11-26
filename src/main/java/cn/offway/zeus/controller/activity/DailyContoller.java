package cn.offway.zeus.controller.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhActivityInfo;
import cn.offway.zeus.domain.PhActivityPrize;
import cn.offway.zeus.domain.PhWxuserInfo;
import cn.offway.zeus.service.PhActivityInfoService;
import cn.offway.zeus.service.PhActivityJoinService;
import cn.offway.zeus.service.PhActivityPrizeService;
import cn.offway.zeus.service.PhWxuserInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 每日福利
 * @author wn
 *
 */
@RestController
@RequestMapping("/activity/daily")
public class DailyContoller {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhActivityInfoService phActivityInfoService;
	
	@Autowired
	private PhWxuserInfoService phWxuserInfoService;
	
	@Autowired
	private PhActivityJoinService phActivityJoinService;
	
	@Autowired
	private PhActivityPrizeService phActivityPrizeService;
	
	@ApiOperation(value = "活动列表")
	@GetMapping("/list")
	public JsonResult list(){
		try {
			return jsonResultHelper.buildSuccessJsonResult(phActivityInfoService.list());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("每日福利活动列表查询异常",e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
	@ApiOperation(value = "活动详情")
	@GetMapping("/detail")
	public JsonResult detail(@ApiParam("活动ID") @RequestParam Long activityId, @ApiParam("用户unionid") @RequestParam String unionid){
		try {
			return jsonResultHelper.buildSuccessJsonResult(phActivityInfoService.detail(activityId,unionid));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("每日福利活动详情查询异常",e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
	@ApiOperation(value = "参与的活动")
	@GetMapping("/user")
	public JsonResult user(@ApiParam("用户unionid") @RequestParam String unionid){
		try {
			return jsonResultHelper.buildSuccessJsonResult(phActivityJoinService.findByunionid(unionid));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("每日福利参与的活动异常",e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
	@ApiOperation(value = "参与活动")
	@PostMapping("/join")
	public JsonResult join(@ApiParam("活动ID") @RequestParam Long activityId, @ApiParam("用户unionid") @RequestParam String unionid){
		try {
			
			PhWxuserInfo phWxuserInfo = phWxuserInfoService.findByUnionid(unionid);
			if(null == phWxuserInfo){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
			}
			PhActivityInfo phActivityInfo = phActivityInfoService.findOne(activityId);
			if(null == phActivityInfo){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
			}
			int count = phActivityJoinService.countByUnionidAndActivityId(unionid, activityId);
			if(count>0){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_PARTICIPATED);
			}
			
			phActivityJoinService.join(phActivityInfo,phWxuserInfo);
			return jsonResultHelper.buildSuccessJsonResult(null);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("每日福利参与异常",e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
	@ApiOperation(value = "填写地址")
	@PostMapping("/addr")
	public JsonResult addr(PhActivityPrize activityPrize){
		
		PhActivityPrize phActivityPrize = phActivityPrizeService.findByActivityIdAndUnionid(activityPrize.getActivityId(), activityPrize.getUnionid());
		if(null == phActivityPrize){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.PRIZE_NOT_EXISTS);
		}
		phActivityPrize.setAddr(activityPrize.getAddr());
		phActivityPrize.setPhone(activityPrize.getPhone());
		phActivityPrize.setRealName(activityPrize.getRealName());
		phActivityPrizeService.save(phActivityPrize);
		return jsonResultHelper.buildSuccessJsonResult(null); 
	}
	
	
	
}
