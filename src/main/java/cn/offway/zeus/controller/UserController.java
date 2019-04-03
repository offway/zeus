package cn.offway.zeus.controller;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.domain.PhWxuserInfo;
import cn.offway.zeus.dto.WxuserInfo;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.service.PhWxuserInfoService;
import cn.offway.zeus.service.SmsService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.IpUtil;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 用户相关
 * @author wn
 *
 */
@Api(tags={"用户"})
@RestController
@RequestMapping("/user")
public class UserController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhWxuserInfoService phWxuserInfoService;
	
	@Autowired
	private SmsService smsService;
	
	@Value("${is-prd}")
	private boolean isPrd;
	
	@Autowired
	private PhUserInfoService phUserInfoService;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	private static final String SMS_CODE_KEY="zeus.sms.code";
	
	@ApiOperation("微信用户信息保存")
	@PostMapping("/wx")
	public JsonResult wx(@ApiParam("微信用户信息") WxuserInfo wxuserInfo){
		try {
			
			PhWxuserInfo phWxuserInfo = phWxuserInfoService.findByUnionid(wxuserInfo.getUnionid());
			if(null == phWxuserInfo){
				phWxuserInfo = new PhWxuserInfo();
			}
			if(StringUtils.isBlank(wxuserInfo.getOpenid())){
				wxuserInfo.setOpenid(phWxuserInfo.getOpenid());
			}
			if(StringUtils.isBlank(wxuserInfo.getAppopenid())){
				wxuserInfo.setAppopenid(phWxuserInfo.getAppopenid());
			}
			if(StringUtils.isBlank(wxuserInfo.getMiniopenid())){
				wxuserInfo.setMiniopenid(phWxuserInfo.getMiniopenid());
			}
			BeanUtils.copyProperties(wxuserInfo, phWxuserInfo);
			phWxuserInfo.setCreateTime(new Date());
			phWxuserInfoService.save(phWxuserInfo);
			return jsonResultHelper.buildSuccessJsonResult(null);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("微信用户信息保存",wxuserInfo.toString(),e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
	@ApiOperation("发送短信验证码")
	@PostMapping("/sms")
	public JsonResult sms(@ApiParam("手机号") @RequestParam String phone,HttpServletRequest request){
		int code = 123456;
		if(isPrd){
			code = RandomUtils.nextInt(100000, 999999);
		}
		boolean result = smsService.sendMsg(phone, "【OFFWAY】验证码："+code+"。此验证码只用于登陆OFFWAY验证身份，请勿转发他人，10分钟内有效。",IpUtil.getIpAddr(request));
		if(result){
    		stringRedisTemplate.opsForValue().set(SMS_CODE_KEY+"_"+phone, ""+code, 10, TimeUnit.MINUTES);
			return jsonResultHelper.buildSuccessJsonResult(null);
		}else{
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_FAIL);
		}
	}
	
	@ApiOperation("注册")
	@PostMapping("/register")
	public JsonResult register(
			@ApiParam("手机号") @RequestParam String phone,
			@ApiParam("验证码") @RequestParam String code,
			@ApiParam("微信用户ID") @RequestParam(required=false) String unionid,
			@ApiParam("微博ID") @RequestParam(required=false) String weiboid,
			@ApiParam("QQID") @RequestParam(required=false) String qqid,
			@ApiParam("用户昵称") @RequestParam(required=false) String nickName,
			@ApiParam("头像") @RequestParam(required=false) String headimgurl){
		
    	String smsCode = stringRedisTemplate.opsForValue().get(SMS_CODE_KEY+"_"+phone);
    	if(StringUtils.isBlank(smsCode)){
    		return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_INVALID);
    	}
    	
    	if(!code.equals(smsCode)){
    		return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_ERROR);
    	}

		
		if(StringUtils.isNotBlank(phone) && null!=phUserInfoService.findByPhone(phone)){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_EXISTS);
		}
		
		if(StringUtils.isNotBlank(weiboid) && null!=phUserInfoService.findByWeiboid(weiboid)){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_EXISTS);
		}
		
		if(StringUtils.isNotBlank(unionid) && null!=phUserInfoService.findByUnionid(unionid)){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_EXISTS);
		}
		
		if(StringUtils.isNotBlank(qqid) && null!=phUserInfoService.findByQqid(qqid)){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_EXISTS);
		}
		
		PhUserInfo phUserInfo = new PhUserInfo();
		phUserInfo.setPhone(phone);
		if(StringUtils.isNotBlank(phone)){
			nickName = StringUtils.isBlank(nickName)?"OFFWAY_"+phone.substring(5):nickName;
		}
		phUserInfo.setNickname(nickName);
		phUserInfo.setHeadimgurl(headimgurl);
		phUserInfo.setWeiboid(weiboid);
		phUserInfo.setQqid(qqid);
		phUserInfo.setUnionid(unionid);
		phUserInfo.setBalance(0D);
		phUserInfo.setSex("1");
		phUserInfo.setVersion(0L);
		phUserInfo.setVoucherCount(0L);
		phUserInfo.setCollectCount(0L);
		phUserInfo.setCreateTime(new Date());
		phUserInfo = phUserInfoService.save(phUserInfo);
		return jsonResultHelper.buildSuccessJsonResult(phUserInfo);

	}
	
	@ApiOperation("登录")
	@PostMapping("/login")
	public JsonResult login(
			@ApiParam("手机号") @RequestParam(required=false) String phone,
			@ApiParam("验证码") @RequestParam(required=false) String code,
			@ApiParam("微信用户ID") @RequestParam(required=false) String unionid,
			@ApiParam("微博ID") @RequestParam(required=false) String weiboid,
			@ApiParam("QQID") @RequestParam(required=false) String qqid){
		
		PhUserInfo phUserInfo = null;
		if(StringUtils.isNotBlank(phone)){
			
			String smsCode = stringRedisTemplate.opsForValue().get(SMS_CODE_KEY+"_"+phone);
			if(StringUtils.isBlank(smsCode)){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_INVALID);
			}
			
			if(!code.equals(smsCode)){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_ERROR);
			}
			phUserInfo = phUserInfoService.findByPhone(phone);
			if(null==phUserInfo){
				phUserInfo = new PhUserInfo();
				phUserInfo.setPhone(phone);
				phUserInfo.setNickname("OFFWAY_"+phone.substring(5));
				phUserInfo.setBalance(0D);
				phUserInfo.setSex("1");
				phUserInfo.setVersion(0L);
				phUserInfo.setVoucherCount(0L);
				phUserInfo.setCollectCount(0L);
				phUserInfo.setCreateTime(new Date());
				phUserInfo = phUserInfoService.save(phUserInfo);
			}
		}
		
		if(StringUtils.isNotBlank(unionid)){
			phUserInfo = phUserInfoService.findByUnionid(unionid);
			if(null==phUserInfo){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
			}
		}
		
		if(StringUtils.isNotBlank(weiboid)){
			phUserInfo = phUserInfoService.findByWeiboid(weiboid);
			if(null==phUserInfo){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
			}
		}
		
		if(StringUtils.isNotBlank(qqid)){
			phUserInfo = phUserInfoService.findByQqid(qqid);
			if(null==phUserInfo){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
			}
		}
		
		return jsonResultHelper.buildSuccessJsonResult(phUserInfo);

	}
	
	@ApiOperation("更新用户信息")
	@PostMapping("/update")
	public JsonResult update(@RequestBody @ApiParam("用户信息")  PhUserInfo userInfo){
		
		PhUserInfo phUserInfo = phUserInfoService.findOne(userInfo.getId());
		
		if(null==phUserInfo){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
		}
		phUserInfo.setBirthday(userInfo.getBirthday());
		phUserInfo.setHeadimgurl(userInfo.getHeadimgurl());
		phUserInfo.setHeight(userInfo.getHeight());
		phUserInfo.setNickname(userInfo.getNickname());
		phUserInfo.setSex(userInfo.getSex());
		phUserInfo.setWeight(userInfo.getWeight());
		PhUserInfo phUserInfo1 = phUserInfoService.save(phUserInfo);
		return jsonResultHelper.buildSuccessJsonResult(phUserInfo1);

	}
	
	@ApiOperation("绑定第三方登录账号")
	@PostMapping("/bind")
	public JsonResult login(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("微信用户ID") @RequestParam(required=false) String unionid,
			@ApiParam("微博ID") @RequestParam(required=false) String weiboid,
			@ApiParam("QQID") @RequestParam(required=false) String qqid){
		
		PhUserInfo phUserInfo = phUserInfoService.findOne(userId);
		if(null==phUserInfo){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
		}
		phUserInfo.setWeiboid(weiboid);
		phUserInfo.setQqid(qqid);
		phUserInfo.setUnionid(unionid);
		phUserInfo = phUserInfoService.save(phUserInfo);
		return jsonResultHelper.buildSuccessJsonResult(phUserInfo);

	}
	
	@SuppressWarnings("unchecked")
	@ApiOperation("我的")
	@GetMapping("/info")
	public JsonResult info(@ApiParam("用户ID") @RequestParam Long userId){
		PhUserInfo phUserInfo = phUserInfoService.findOne(userId);
		if(null==phUserInfo){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
		}
		String userInfo = JSON.toJSONString(phUserInfo,SerializerFeature.WriteMapNullValue);
		Map<String, Object> map = JSON.parseObject(userInfo, Map.class);
		//TODO 需查询订单信息
		map.put("pendingPayment", 0L);
		map.put("pendingShip", 0L);
		map.put("pendingReceipt", 0L);
		map.put("goodsReturn", 0L);
		return jsonResultHelper.buildSuccessJsonResult(map);

	}
	
	
	
}
