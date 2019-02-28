package cn.offway.zeus.controller.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhInviteRecord;
import cn.offway.zeus.domain.PhLotteryTicket;
import cn.offway.zeus.domain.PhProductInfo;
import cn.offway.zeus.domain.PhWinningRecord;
import cn.offway.zeus.domain.PhWxuserInfo;
import cn.offway.zeus.enums.TicketSourceEnum;
import cn.offway.zeus.service.PhInviteRecordService;
import cn.offway.zeus.service.PhLotteryTicketService;
import cn.offway.zeus.service.PhProductInfoService;
import cn.offway.zeus.service.PhShareRecordService;
import cn.offway.zeus.service.PhWinningRecordService;
import cn.offway.zeus.service.PhWxuserInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.HttpClientUtil;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 抽奖活动相关
 * 
 * @author wn
 *
 */
@RestController
@RequestMapping("/activity/lottery")
public class LotteryController {

	@Autowired
	private JsonResultHelper jsonResultHelper;

	@Autowired
	private PhLotteryTicketService phLotteryTicketService;

	@Autowired
	private PhProductInfoService phProductInfoService;
	
	@Autowired
	private PhInviteRecordService phInviteRecordService;
	
	@Autowired
	private PhWxuserInfoService phWxuserInfoService;
	
	@Autowired
	private PhWinningRecordService phWinningRecordService;
	
	@Autowired
	private PhShareRecordService phShareRecordService;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${app.url}")
	private String appUrl;

	/**
	 * 登记抽奖
	 * 
	 * @return
	 */
	@ApiOperation(value = "登记抽奖")
	@PostMapping("/register")
	public JsonResult register(@ApiParam("活动ID") @RequestParam Long productId, @ApiParam("用户unionid") @RequestParam String unionid,
			@ApiParam("邀请人unionid") @RequestParam(required = false) String inviteUnionid,@ApiParam("表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的 prepay_id") @RequestParam(required = false) String formId) {

		try {
			// 检查活动时间
			PhProductInfo phProductInfo = phProductInfoService.findOne(productId);
			Date now = new Date();
			if (now.before(phProductInfo.getBeginTime()) || now.after(phProductInfo.getEndTime())) {
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_END);
			}
			
			PhWxuserInfo phWxuserInfo = phWxuserInfoService.findByUnionid(unionid);
			if(null == phWxuserInfo){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
			}
			
			//检查用户是否已经参与活动
			int participates = phLotteryTicketService.countByProductIdAndUnionidAndSource(productId, unionid, TicketSourceEnum.JOIN.getCode());
			if(participates > 0){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_PARTICIPATED);
			}
			
			PhWxuserInfo iphWxuserInfo = null;
			if(StringUtils.isNotBlank(inviteUnionid)){
				iphWxuserInfo = phWxuserInfoService.findByUnionid(inviteUnionid);
				if(null == iphWxuserInfo){
					return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
				}
			}
			phLotteryTicketService.register(productId, phWxuserInfo, iphWxuserInfo,formId);
			return jsonResultHelper.buildSuccessJsonResult(null);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("登记抽奖异常",e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
	@ApiOperation(value = "查询用户抽奖券明细")
	@GetMapping("/tickets")
	public List<PhLotteryTicket> findTickets(@ApiParam("活动ID") @RequestParam Long productId, @ApiParam("用户unionid") @RequestParam String unionid){
		return phLotteryTicketService.findByProductIdAndUnionid(productId, unionid);
	}
	
	@ApiOperation(value = "查询用户邀请好友列表")
	@GetMapping("/invites")
	public List<PhInviteRecord> findInvites(@ApiParam("活动ID") @RequestParam Long productId, @ApiParam("用户unionid") @RequestParam String unionid){
		return phInviteRecordService.findByProductIdAndUnionid(productId, unionid);
	}
	
	@ApiOperation(value = "检查用户是否已参加活动")
	@GetMapping("/check")
	public JsonResult check(@ApiParam("活动ID") @RequestParam Long productId, @ApiParam("用户unionid") @RequestParam String unionid){
		//检查用户是否已经参与活动
		int participates = phLotteryTicketService.countByProductIdAndUnionidAndSource(productId, unionid, TicketSourceEnum.JOIN.getCode());
		if(participates > 0){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_PARTICIPATED);
		}
		return jsonResultHelper.buildSuccessJsonResult(null);
	}
	
	@ApiOperation(value = "活动初始化参数")
	@GetMapping("/init")
	public Map<String, Object> init(@ApiParam("活动ID") @RequestParam Long productId,@ApiParam("用户unionid") @RequestParam String unionid){
		Map<String, Object> map = new HashMap<>();
		
		PhProductInfo phProductInfo = phProductInfoService.findOne(productId);
		map.put("product", phProductInfo);
		map.put("beginTime", phProductInfo.getBeginTime());
		map.put("endTime", phProductInfo.getEndTime());
		//查询是否下载APP
		
		String result = HttpClientUtil.get(appUrl+"/api/user/isExistsByUnionid?unionid="+unionid);
		//int count = phLotteryTicketService.countByUnionidAndSource(unionid, TicketSourceEnum.APP_REGISTER.getCode());
		//map.put("appDownloaded", count>0?true:false);
		map.put("appDownloaded", "1".equals(result)?true:false);
		int shareCount = phShareRecordService.countByUnionidAndProductId(unionid, productId);
		//是否分享
		map.put("isShare", shareCount>0?true:false);
		//是否已经参与活动
		int participates = phLotteryTicketService.countByProductIdAndUnionidAndSource(productId, unionid, TicketSourceEnum.JOIN.getCode());
		map.put("isJoin", participates>0?true:false);
		//活动规则
		String ruleContent = phProductInfo.getRuleContent();
		List<String> rule = new ArrayList<>();
		if(StringUtils.isNotBlank(ruleContent)){
			String[] rules = ruleContent.split("\n");
			for (String s : rules) {
				if(StringUtils.isNotBlank(s)){
					rule.add(s);
				}
			}
		}
		map.put("rules", rule);
		
		//APP活动规则
		String appRuleContent = phProductInfo.getAppRuleContent();
		List<String> ruleList = new ArrayList<>();
		if(StringUtils.isNotBlank(appRuleContent)){
			String[] rules = appRuleContent.split("\n");
			for (String s : rules) {
				if(StringUtils.isNotBlank(s)){
					ruleList.add(s);
				}
			}
		}
		map.put("appRules", ruleList);
		return map;
		
	}
	
	
	@ApiOperation(value = "中奖用户列表")
	@GetMapping("/winRecord")
	public List<PhWinningRecord> winRecord(@ApiParam("活动ID") @RequestParam Long productId){
		return phWinningRecordService.findByProductId(productId);
	}
	
	@ApiOperation(value = "保存分享记录")
	@PostMapping("/saveShare")
	public JsonResult saveShare(@ApiParam("活动ID") @RequestParam Long productId,@ApiParam("用户unionid") @RequestParam String unionid,
			@ApiParam("渠道") @RequestParam(required = false) String channel){
		try {
			// 检查活动时间
			PhProductInfo phProductInfo = phProductInfoService.findOne(productId);
			Date now = new Date();
			if (now.before(phProductInfo.getBeginTime()) || now.after(phProductInfo.getEndTime())) {
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_END);
			}
			phShareRecordService.saveShare(productId,unionid,channel);
			return jsonResultHelper.buildSuccessJsonResult(null);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存分享记录异常",e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
		
	}
	
	@ApiOperation(value = "更新formId")
	@PostMapping("/updateFormId")
	public JsonResult updateFormId(@ApiParam("活动ID") @RequestParam Long productId, @ApiParam("用户unionid") @RequestParam String unionid,
			@ApiParam("表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的 prepay_id") @RequestParam String formId ){
		
		phLotteryTicketService.updateFormId(productId, unionid, formId);
		
		return jsonResultHelper.buildSuccessJsonResult(null);
		
	}
	
	@ApiOperation(value = "APP注册送券")
	@PostMapping("/appTicket")
	public JsonResult appTicket(String unionid){
		try {
			phLotteryTicketService.appTicket(unionid);
			return jsonResultHelper.buildSuccessJsonResult(null);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("APP注册送券异常",e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
		
	}

}
