package cn.offway.zeus.controller;

import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import cn.offway.zeus.domain.*;
import cn.offway.zeus.service.*;
import cn.offway.zeus.utils.*;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import cn.offway.zeus.dto.WxuserInfo;
import cn.offway.zeus.repository.PhInviteInfoRepository;
import cn.offway.zeus.repository.PhRefundRepository;
import cn.offway.zeus.repository.PhShoppingCartRepository;
import cn.offway.zeus.repository.PhVoucherInfoRepository;
import cn.offway.zeus.service.impl.PhVoucherInfoServiceImpl;
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
	
	@Autowired
	private PhCollectService phCollectService;
	
	@Autowired
	private VCollectBrandService vCollectBrandService;
	
	@Autowired
	private VCollectGoodsService vCollectGoodsService;
	
	@Autowired
	private PhShoppingCartRepository phShoppingCartRepository;
	
	@Autowired
	private PhPreorderInfoService phPreorderInfoService;
	
	@Autowired
	private PhInviteInfoRepository phInviteInfoRepository;
	
	@Autowired
	private PhOrderInfoService phOrderInfoService;
	
	@Autowired
	private PhCapitalFlowService phCapitalFlowService;
	
	@Autowired
	private PhNoticeService phNoticeService;
	
	@Autowired
	private PhVoucherInfoRepository phVoucherInfoRepository;
	
	@Autowired
	private PhUserChannelService phUserChannelService;
	
	@Autowired
	private PhRefundRepository phRefundRepository;

	@Autowired
	private PhWithdrawInfoService phWithdrawInfoService;

	@Autowired
	private PhGoodsService phGoodsService;

	
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
			if(null!=wxuserInfo){
				BeanUtils.copyProperties(wxuserInfo, phWxuserInfo);
			}
			phWxuserInfo.setCreateTime(new Date());
			phWxuserInfoService.save(phWxuserInfo);
			return jsonResultHelper.buildSuccessJsonResult(null);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("微信用户信息保存",wxuserInfo.toString(),e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}

	@ApiOperation("发送短信验证码-活动")
	@PostMapping("/sms-act")
	public JsonResult smsAct(@ApiParam("手机号") @RequestParam String phone,HttpServletRequest request){
		/**
		 * 虚拟运营商号段：
		 * 电信：1700、1701、1702
		 * 移动：1703、1705、1706
		 * 联通：1704、1707、1708、1709、171
		 */
		boolean check = phone.startsWith("1700")||phone.startsWith("1701")||phone.startsWith("1702")
				||phone.startsWith("1703")||phone.startsWith("1705")||phone.startsWith("1706")
				||phone.startsWith("1704")||phone.startsWith("1707")||phone.startsWith("1708")
				||phone.startsWith("1709")||phone.startsWith("171");
		if(check){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_FAIL);
		}
		return sms(phone,request);
	}
	
	@ApiOperation("发送短信验证码")
	@PostMapping("/sms")
	public JsonResult sms(@ApiParam("手机号") @RequestParam String phone,HttpServletRequest request){
		int code = 123456;
		
		phone = phone.contains("+")?phone:"+86"+phone;
		
		//慢慢的手机号为了审核需要特殊处理
		if(isPrd && !"+8618016388248".equals(phone)){
			code = RandomUtils.nextInt(100000, 999999);
		}
		String msg = "【HENCHAO】Verification code: "+code+". This verification code is only used to verify the identity by logging in to HENCHAO. Please do not forward it to others and it will be valid within 10 minutes. ";

		//大陆、台湾、香港、澳门
		if(phone.indexOf("+")<0 || phone.startsWith("+86") || phone.startsWith("+852")
				|| phone.startsWith("+853")|| phone.startsWith("+886")){
			msg = "【很潮】验证码："+code+"。此验证码只用于登陆 很潮 验证身份，请勿转发他人，10分钟内有效。";
		}
		
		boolean result = smsService.sendMsg(phone, msg, IpUtil.getIpAddr(request));

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
			@ApiParam("头像") @RequestParam(required=false) String headimgurl,
			@ApiParam("邀请用户ID") @RequestParam(required=false) Long inviteUserId){
		
		phone = phone.contains("+")?phone:"+86"+phone;
		unionid = StringUtils.isBlank(unionid)?null:unionid;

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
		
		return jsonResultHelper.buildSuccessJsonResult(phUserInfoService.register(phone, unionid, weiboid, qqid, nickName, headimgurl, inviteUserId,null));

	}

	@ApiOperation("小程序注册/登录")
	@PostMapping("/register/mini")
	public JsonResult register(
	        @ApiParam("微信用户ID") @RequestParam String unionid,
            @ApiParam("微信用户昵称") @RequestParam String nickName,
            @ApiParam("微信用户头像") @RequestParam String headimgurl,
            @ApiParam("session_key") @RequestParam String sessionKey,
            @ApiParam("encryptedData,获取手机号得到") @RequestParam String encryptedData,
            @ApiParam("iv,获取手机号得到") @RequestParam String iv){

        try {

            PhUserInfo phUserInfo = null;
            if(StringUtils.isNotBlank(unionid)){
                phUserInfo = phUserInfoService.findByUnionid(unionid);
                if(null!=phUserInfo){
                    return jsonResultHelper.buildSuccessJsonResult(phUserInfo);
                }
            }

            String result = AesCbcUtil.decrypt(encryptedData, sessionKey, iv, "UTF-8");
            logger.info("解密小程序获取手机号信息:"+result);
            JSONObject jsonObject = JSON.parseObject(result);
            String purePhoneNumber = jsonObject.getString("purePhoneNumber");
            String countryCode = jsonObject.getString("countryCode");
            StringBuilder sb = new StringBuilder();
            sb.append("+").append(countryCode).append(purePhoneNumber);
            String phone = sb.toString();

            phUserInfo = phUserInfoService.findByPhone(phone);
            if(null!=phUserInfo){
                return jsonResultHelper.buildSuccessJsonResult(phUserInfo);
            }
            return jsonResultHelper.buildSuccessJsonResult(phUserInfoService.register(phone, unionid, null, null, nickName, headimgurl, null,null));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("小程序注册异常",e);
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
        }
    }

	@ApiOperation("一键登录")
	@PostMapping("/login/oneKey")
	public JsonResult loginOneKey(@ApiParam("LoginToken") @RequestParam String loginToken){

		try {
			String result = HttpClientUtil.post(
							"https://api.verification.jpush.cn/v1/web/loginTokenVerify",
							"{\"loginToken\":\""+loginToken+"\",\"exID\":\"\"}",
							"Authorization",
							"Basic MjNmM2QxNDc2NTc5ZWRhMTlhMGRhODcyOjkwZjRhNjk1NzVkNGE1MWM4NzdmNWRhZA==");
			JSONObject jsonObject = JSON.parseObject(result);
			String phoneEncrypted = jsonObject.getString("phone");

			String prikey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBANjS5OIM2guSO1hc" +
					"0dXCXXjhEFSaNvnxVAV8P6zOpki7ZpN7JV4WXhUYzuB1ju0/9LMDQSzYPBqvKXp0" +
					"kZ9L8LEoedZayYzYmR45OwZcSOiTEnJYC6Sg2jxtI2b/hhqoOAfzfrEelpdeNgV1" +
					"C4EhEqKVSBHACDBU3A2/1+tCxqPLAgMBAAECgYA5Hq3fg0U6AScTKzi4WIDpZFk6" +
					"AHp1NAdPfqEDtFkIFh56wdlhRQE6C5QMe8vQYqXjNvtHhhunZ/fEY8stLQNWHE0r" +
					"oFQz/lDhzCoIA0nyXwSOZF0bKuuKM3twM+Ns9ync+fTFhljKMTjF4pomZlKwK4EJ" +
					"JBZBsowbpCYHDhBi4QJBAO/n/JqYTs7NecL2FtpiYSi6HL1iGfx+hS3DKTlu8ygp" +
					"078gGRkvX4SQCcrre2C4iPYZ51YtTE73EOBe/loYIzECQQDnXoDdvxgSWlenZVxx" +
					"ElmNzJPOtwMWQQn8WGbyB8e4Lkn5axnsuaq8JExs5qtAQRfiVPXNlIWPCeQfKYUo" +
					"gh+7AkAZZbvOOfWN2x7azuaYc/XJM/q66dnKazJ6J8EDfYVsaRErmKBPlD5OcFk5" +
					"DDjhgmetdgyRiPYdHfbBag0PSKLhAkAb1j3w8AXoZ2A2braRkCCgM+XwsAo6Cjc2" +
					"WjiAlDkOSttxm9YxqiEFo+RiEdq2z14dSBWO13i+PfVpXmh7+DPzAkB+1Yk6rogc" +
					"kQ+n41U62tOJC5HMOtpQpmnxZDorIs71oz0Zmr+A4VqsCV4pgFOKIqQGVuzZYRAa" +
					"vG+O1j7VseUr";
			String phone = RSADecrypt.decrypt(phoneEncrypted,prikey);
			if(StringUtils.isNotBlank(phone)){
				phone = "+86"+phone;
				PhUserInfo phUserInfo = phUserInfoService.findByPhone(phone);
				if(null == phUserInfo){
					phUserInfo = phUserInfoService.register(phone, null, null, null, null, null, null,null);
				}
				Map<String, Object> map = userData(phUserInfo);
				return jsonResultHelper.buildSuccessJsonResult(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("一键登录异常",e);
		}

		return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
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
			
			phone = phone.contains("+")?phone:"+86"+phone;

			String smsCode = stringRedisTemplate.opsForValue().get(SMS_CODE_KEY+"_"+phone);
			if(StringUtils.isBlank(smsCode)){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_INVALID);
			}
			
			if(!code.equals(smsCode)){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_ERROR);
			}
			phUserInfo = phUserInfoService.findByPhone(phone);
			if(null==phUserInfo){
				//手机号没有直接注册
				phUserInfo = phUserInfoService.register(phone, null, null, null, null, null, null,null);
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
		
		Map<String, Object> map = userData(phUserInfo);
		return jsonResultHelper.buildSuccessJsonResult(map);

	}
	
	@ApiOperation("更新用户信息")
	@PostMapping("/update")
	public JsonResult update(@RequestBody @ApiParam("用户信息")  PhUserInfo userInfo){
		
		PhUserInfo phUserInfo = phUserInfoService.findById(userInfo.getId());
		
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
	public JsonResult bind(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("微信用户ID") @RequestParam(required=false) String unionid,
			@ApiParam("微博ID") @RequestParam(required=false) String weiboid,
			@ApiParam("QQID") @RequestParam(required=false) String qqid){
		
		PhUserInfo phUserInfo = phUserInfoService.findById(userId);
		if(null==phUserInfo){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
		}
		phUserInfo.setWeiboid(weiboid);
		phUserInfo.setQqid(qqid);
		phUserInfo.setUnionid("".equals(unionid)?null:unionid);
		phUserInfo = phUserInfoService.save(phUserInfo);
		return jsonResultHelper.buildSuccessJsonResult(phUserInfo);

	}
	
	@ApiOperation("我的")
	@GetMapping("/home")
	public JsonResult home(@ApiParam("用户ID") @RequestParam Long userId){
		PhUserInfo phUserInfo = phUserInfoService.findById(userId);
		if(null==phUserInfo){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
		}
		Map<String, Object> map = userData(phUserInfo);
		return jsonResultHelper.buildSuccessJsonResult(map);

	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> userData( PhUserInfo phUserInfo) {
		Long userId = phUserInfo.getId();
		//查询优惠券数量
		phUserInfo.setVoucherCount(phVoucherInfoRepository.countByUserId(userId));
		String userInfo = JSON.toJSONString(phUserInfo,SerializerFeature.WriteMapNullValue);
		Map<String, Object> map = JSON.parseObject(userInfo, Map.class);
		
		map.put("pendingPayment", phPreorderInfoService.countByUserIdAndStatus(userId, "0"));
		//0-已下单,1-已付款,2-已发货,3-已收货,4-取消
		map.put("pendingShip", phOrderInfoService.countByUserIdAndStatus(userId, "1"));
		map.put("pendingReceipt", phOrderInfoService.countByUserIdAndStatus(userId, "2"));
		List<PhShoppingCart> phShoppingCarts = phShoppingCartRepository.findByUserIdOrderByCreateTimeDesc(userId);
		map.put("shoppingCartNum", phShoppingCarts.size());
		map.put("noticeNum", phNoticeService.countByUserIdAndIsRead(userId, "0"));
		List<String> status = new ArrayList<>();
		//0-审核中,1-待退货,2-退货中,3-退款中,4-退款成功,5-退款取消,6-审核失败
		status.add("0");
		status.add("1");
		status.add("2");
		status.add("3");
		status.add("6");
		map.put("refundNum", phRefundRepository.countByUserIdAndStatusIn(userId, status));

		return map;
	}
	
	@ApiOperation("用户信息")
	@GetMapping("/info")
	public JsonResult info(@ApiParam("用户ID") @RequestParam Long userId){
		PhUserInfo phUserInfo = phUserInfoService.findById(userId);
		if(null==phUserInfo){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
		}
		return jsonResultHelper.buildSuccessJsonResult(phUserInfo);

	}
	
	@ApiOperation("加入收藏")
	@PostMapping("/collect")
	public JsonResult collect(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("类型[0-商品，1-品牌，2-资讯]") @RequestParam String type,
			@ApiParam("类型相应的ID,如商品ID,品牌ID等") @RequestParam Long matchId){
		try {
			if("0".equals(type)){
				//限量商品不能收藏
				PhGoods phGoods = phGoodsService.findById(matchId);
				if("1".equals(phGoods.getLabel())){
					return jsonResultHelper.buildFailJsonResult(CommonResultCode.LIMITEDSALE_NOT_COLLECT);
				}
			}

			Long id = phCollectService.collect(userId, type, matchId);
			return jsonResultHelper.buildSuccessJsonResult(id);
		}catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.COLLECT_EXISTS);
		} catch (Exception e) {
			e.printStackTrace();
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);

		}

	}
	
	@ApiOperation("我的收藏")
	@GetMapping("/collect")
	public JsonResult collects(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("类型[0-商品，1-品牌，2-资讯]") @RequestParam String type,
			@ApiParam("页码,从0开始") @RequestParam int page,
			@ApiParam("页大小") @RequestParam int size){
		
		if("0".equals(type)){
			return jsonResultHelper.buildSuccessJsonResult(vCollectGoodsService.findByPage(userId, PageRequest.of(page, size)));
		}else if("1".equals(type)){
			return jsonResultHelper.buildSuccessJsonResult(vCollectBrandService.findByPage(userId, PageRequest.of(page, size)));
		}

		return jsonResultHelper.buildSuccessJsonResult(null);

	}
	
	@ApiOperation("删除收藏")
	@DeleteMapping("/collect")
	public JsonResult collect(@ApiParam("收藏ID") @RequestParam Long collectId){
		phCollectService.deleteById(collectId);
		return jsonResultHelper.buildSuccessJsonResult(null);
	}
	
	@ApiOperation("查看是否收藏")
	@GetMapping("/isCollect")
	public JsonResult isCollect(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("类型[0-商品，1-品牌，2-资讯]") @RequestParam String type,
			@ApiParam("对应的ID,如商品ID、品牌ID等") @RequestParam Long matchId){
		return jsonResultHelper.buildSuccessJsonResult(phCollectService.isCollect(userId, type, matchId));
	}
	
	@ApiOperation("成为赚钱达人")
	@PostMapping("/mm")
	public  JsonResult mm(@ApiParam("用户ID") @RequestParam Long userId){
		PhUserInfo phUserInfo = phUserInfoService.findById(userId);
		phUserInfo.setIsMm("1");
		phUserInfoService.save(phUserInfo);
		return jsonResultHelper.buildSuccessJsonResult(null);

	}
	
	@ApiOperation("粉丝达人")
	@GetMapping("/invites")
	public  JsonResult invites(@ApiParam("用户ID") @RequestParam Long userId){
		return jsonResultHelper.buildSuccessJsonResult(phInviteInfoRepository.invites(userId));

	}
	
	@ApiOperation("返现记录")
	@GetMapping("/returnAmounts")
	public  JsonResult returnAmounts(@ApiParam("用户ID") @RequestParam Long userId){
		return jsonResultHelper.buildSuccessJsonResult(phCapitalFlowService.findByBusinessTypeAndUserIdOrderByCreateTimeDesc("0", userId));
	}
	
	@ApiOperation("渠道登记")
	@PostMapping("/registerCh")
	public  JsonResult registerCh(
			@ApiParam("手机号") @RequestParam String phone,
			@ApiParam("验证码") @RequestParam String code,
			@ApiParam("渠道[HY-欢阅传媒]") @RequestParam(required = false) String channel){
		
		phone = phone.contains("+")?phone:"+86"+phone;

    	String smsCode = stringRedisTemplate.opsForValue().get(SMS_CODE_KEY+"_"+phone);
    	if(StringUtils.isBlank(smsCode)){
    		return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_INVALID);
    	}
    	
    	if(!code.equals(smsCode)){
    		return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_ERROR);
    	}
		
		if(null!=phUserInfoService.findByPhone(phone)){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_EXISTS);
		}

		/*PhUserChannel phUserChannel = new PhUserChannel();
		phUserChannel.setChannel(channel);
		phUserChannel.setCreateTime(new Date());
		phUserChannel.setPhone(phone);
		phUserChannelService.save(phUserChannel);*/

		phUserInfoService.register(phone, null, null, null, null, null, null,channel);
		return jsonResultHelper.buildSuccessJsonResult(null);
	}

	@ApiOperation("资金流水")
	@GetMapping("/capitalFlow")
	public JsonResult capitalFlow(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("页码,从0开始") @RequestParam int page,
			@ApiParam("页大小") @RequestParam int size){

		return jsonResultHelper.buildSuccessJsonResult(phCapitalFlowService.findByPage(userId,PageRequest.of(page,size)));
	}

	@ApiOperation("提现申请")
	@PostMapping("/withdraw")
	public JsonResult withdraw(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("提现金额[单位:元]") @RequestParam double amount,
            @ApiParam("验证码") @RequestParam String code){
		if(amount < 100D || amount > 20000D){
			return  jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
		}

        PhUserInfo phUserInfo = phUserInfoService.findById(userId);
        String phone = phUserInfo.getPhone();

        String smsCode = stringRedisTemplate.opsForValue().get(SMS_CODE_KEY+"_"+phone);
        if(StringUtils.isBlank(smsCode)){
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_INVALID);
        }

        if(!code.equals(smsCode)){
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_ERROR);
        }

		return phWithdrawInfoService.withdraw(userId, amount);
	}

	@ApiOperation("提现记录")
	@GetMapping("/withdraw")
	public JsonResult withdraw(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("页码,从0开始") @RequestParam int page,
			@ApiParam("页大小") @RequestParam int size){
		return jsonResultHelper.buildSuccessJsonResult(phWithdrawInfoService.findByPage(userId,PageRequest.of(page,size)));
	}


}
