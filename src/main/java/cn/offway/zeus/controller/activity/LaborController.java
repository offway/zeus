package cn.offway.zeus.controller.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhLabor;
import cn.offway.zeus.repository.PhLaborLuckyRepository;
import cn.offway.zeus.service.PhLaborLuckyService;
import cn.offway.zeus.service.PhLaborService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 劳动节活动
 * @author wn
 *
 */
@Api(tags={"劳动节活动"})
@RestController
@RequestMapping("/activity/labor")
public class LaborController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhLaborService phLaborService;
	
	@Autowired
	private PhLaborLuckyService phLaborLuckyService;
	
	@ApiOperation("签到搬砖")
	@PostMapping("/sign")
	public JsonResult sign(@ApiParam("用户ID") @RequestParam Long userId){
		boolean result = phLaborService.sign(userId);
		if(result){
			return jsonResultHelper.buildSuccessJsonResult(null);
		}
		return jsonResultHelper.buildFailJsonResult(CommonResultCode.SIGNED);

	}
	
	@ApiOperation("查询参与详情")
	@GetMapping("/info")
	public JsonResult info(@ApiParam("用户ID") @RequestParam Long userId){
		PhLabor phLabor = phLaborService.findByUserId(userId);
		return jsonResultHelper.buildSuccessJsonResult(phLabor);
	}
	
	@ApiOperation("抽奖")
	@PostMapping("/lottery")
	public JsonResult lottery(@ApiParam("用户ID") @RequestParam Long userId){
		try {
			return phLaborService.lottery(userId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("抽奖异常,userId：{}",userId,e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
	@ApiOperation("我的奖品")
	@GetMapping("/luckly/user")
	public JsonResult lucklyuser(@ApiParam("用户ID") @RequestParam Long userId){
		return jsonResultHelper.buildSuccessJsonResult(phLaborLuckyService.findByUserIdOrderByCreateTimeDesc(userId));
	}
	
	@ApiOperation("获奖记录")
	@GetMapping("/luckly/list")
	public JsonResult lucklylist(){
		return jsonResultHelper.buildSuccessJsonResult(phLaborLuckyService.findByOrderByCreateTimeDesc());
	}

}
