package cn.offway.zeus.controller.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhActivityInfo;
import cn.offway.zeus.service.PhActivityInfoService;
import cn.offway.zeus.service.PhActivityJoinService;
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
	private PhActivityJoinService phActivityJoinService;
	
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
	
}
