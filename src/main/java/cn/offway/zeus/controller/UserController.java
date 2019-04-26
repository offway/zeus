package cn.offway.zeus.controller;

import java.util.Date;
import java.util.List;
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

import cn.offway.zeus.domain.PhOrderInfo;
import cn.offway.zeus.domain.PhShoppingCart;
import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.domain.PhWxuserInfo;
import cn.offway.zeus.dto.WxuserInfo;
import cn.offway.zeus.repository.PhInviteInfoRepository;
import cn.offway.zeus.repository.PhShoppingCartRepository;
import cn.offway.zeus.repository.PhVoucherInfoRepository;
import cn.offway.zeus.service.PhCapitalFlowService;
import cn.offway.zeus.service.PhCollectService;
import cn.offway.zeus.service.PhNoticeService;
import cn.offway.zeus.service.PhOrderInfoService;
import cn.offway.zeus.service.PhPreorderInfoService;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.service.PhVoucherInfoService;
import cn.offway.zeus.service.PhWxuserInfoService;
import cn.offway.zeus.service.SmsService;
import cn.offway.zeus.service.VCollectBrandService;
import cn.offway.zeus.service.VCollectGoodsService;
import cn.offway.zeus.service.impl.PhVoucherInfoServiceImpl;
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
		//慢慢的手机号为了审核需要特殊处理
		if(isPrd && !"18016388248".equals(phone)){
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
			@ApiParam("头像") @RequestParam(required=false) String headimgurl,
			@ApiParam("邀请用户ID") @RequestParam(required=false) Long inviteUserId){
		
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
		
		return jsonResultHelper.buildSuccessJsonResult(phUserInfoService.register(phone, unionid, weiboid, qqid, nickName, headimgurl, inviteUserId));

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
				//手机号没有直接注册
				phUserInfo = phUserInfoService.register(phone, null, null, null, null, null, null);
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
	
	@ApiOperation("我的")
	@GetMapping("/home")
	public JsonResult home(@ApiParam("用户ID") @RequestParam Long userId){
		PhUserInfo phUserInfo = phUserInfoService.findOne(userId);
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
		phUserInfo.setVoucherCount(phVoucherInfoRepository.countByUserIdAndStatus(userId, "0"));
		String userInfo = JSON.toJSONString(phUserInfo,SerializerFeature.WriteMapNullValue);
		Map<String, Object> map = JSON.parseObject(userInfo, Map.class);
		
		map.put("pendingPayment", phPreorderInfoService.countByUserIdAndStatus(userId, "0"));
		//0-已下单,1-已付款,2-已发货,3-已收货,4-取消
		map.put("pendingShip", phOrderInfoService.countByUserIdAndStatus(userId, "1"));
		map.put("pendingReceipt", phOrderInfoService.countByUserIdAndStatus(userId, "2"));
		map.put("goodsReturn", 0L);
		List<PhShoppingCart> phShoppingCarts = phShoppingCartRepository.findByUserIdOrderByCreateTimeDesc(userId);
		map.put("shoppingCartNum", phShoppingCarts.size());
		map.put("noticeNum", phNoticeService.countByUserIdAndIsRead(userId, "0"));
		return map;
	}
	
	@ApiOperation("用户信息")
	@GetMapping("/info")
	public JsonResult info(@ApiParam("用户ID") @RequestParam Long userId){
		PhUserInfo phUserInfo = phUserInfoService.findOne(userId);
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
			return jsonResultHelper.buildSuccessJsonResult(vCollectGoodsService.findByPage(userId, new PageRequest(page, size)));
		}else if("1".equals(type)){
			return jsonResultHelper.buildSuccessJsonResult(vCollectBrandService.findByPage(userId, new PageRequest(page, size)));
		}

		return jsonResultHelper.buildSuccessJsonResult(null);

	}
	
	@ApiOperation("删除收藏")
	@DeleteMapping("/collect")
	public JsonResult collect(@ApiParam("收藏ID") @RequestParam Long collectId){
		phCollectService.delete(collectId);
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
		PhUserInfo phUserInfo = phUserInfoService.findOne(userId);
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
	
}
