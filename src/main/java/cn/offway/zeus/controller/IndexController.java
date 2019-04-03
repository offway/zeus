package cn.offway.zeus.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.convert.IndexedData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.offway.zeus.domain.PhGoods;
import cn.offway.zeus.domain.PhStarsameImage;
import cn.offway.zeus.domain.PhWxuserInfo;
import cn.offway.zeus.service.PhBannerService;
import cn.offway.zeus.service.PhGoodsService;
import cn.offway.zeus.service.PhStarsameImageService;
import cn.offway.zeus.service.PhWxuserInfoService;
import cn.offway.zeus.service.WxService;
import cn.offway.zeus.utils.HttpClientUtil;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import cn.offway.zeus.utils.WechatUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 首页
 * @author wn
 *
 */
@Controller
@Api(tags={"首页"})
public class IndexController {

	@Value("${wx.appid}")
	private String APPID;

	@Value("${wx.secret}")
	private String SECRET;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhWxuserInfoService phWxuserInfoService;
	
	@Autowired
	private WxService wxService;
	
	@Autowired
	private PhBannerService phBannerService;
	
	@Autowired
	private PhStarsameImageService phStarsameImageService;
	
	@Autowired
	private PhGoodsService phGoodsService;

	@ResponseBody
	@GetMapping("/")
	@ApiOperation(value = "欢迎页")
	public String index() {
		return "欢迎访问OFFWAY API服务";
	}

	/**
	 * 微信授权
	 * @param url
	 * @param code
	 * @return
	 */
	@ResponseBody
	@ApiOperation(value = "微信授权-根据授权码获取用户信息")
	@PostMapping("/access/wx")
	public Object wxAccess(@ApiParam("授权码") @RequestParam String code) {

		String accessTokenResult = HttpClientUtil.get("https://api.weixin.qq.com/sns/oauth2/access_token?appid="
				+ APPID + "&secret=" + SECRET + "&code=" + code + "&grant_type=authorization_code");

		JSONObject accessTokenMap = JSON.parseObject(accessTokenResult);

		String accessToken = String.valueOf(accessTokenMap.get("access_token"));
		String openid = String.valueOf(accessTokenMap.get("openid"));

		String userInfoStr = HttpClientUtil.get("https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken
				+ "&openid=" + openid + "&lang=zh_CN");
		
		PhWxuserInfo phWxuserInfo = JSON.parseObject(userInfoStr, PhWxuserInfo.class);
		if(StringUtils.isNotBlank(phWxuserInfo.getOpenid())){
			//如果存在授权用户信息则更新
			PhWxuserInfo oldphWxuserInfo = phWxuserInfoService.findByUnionid(phWxuserInfo.getUnionid());
			phWxuserInfo.setCreateTime(new Date());
			if(null != oldphWxuserInfo){
				phWxuserInfo.setId(oldphWxuserInfo.getId());
				phWxuserInfo.setAppopenid(oldphWxuserInfo.getAppopenid());
				phWxuserInfo.setMiniopenid(oldphWxuserInfo.getMiniopenid());
			}
			return phWxuserInfoService.save(phWxuserInfo);
		}else{
			return JSON.parseObject(userInfoStr);
		}
	}

	@ResponseBody
	@ApiOperation(value = "微信授权-获取JS-SDK配置")
	@PostMapping("/access/wx/config")
	public Map<String, String> getJssdkinfo(String url) {
		return wxService.getJssdkinfo(url);
	}
	
	@GetMapping("/check/{state}")
	public String check(@PathVariable String state){
		return "redirect:"+WechatUtil.getCodeRequest("https://zeus.offway.cn/result", "snsapi_base", APPID, state);
	}
	
	@GetMapping("/result")
	@ResponseBody
	public String result(String code,String state){
		String accessTokenResult = HttpClientUtil.get("https://api.weixin.qq.com/sns/oauth2/access_token?appid="
				+ APPID + "&secret=" + SECRET + "&code=" + code + "&grant_type=authorization_code");

		logger.info("用户核实结果-标识:{},结果:{}",state,accessTokenResult);
		return "中奖用户核实操作已完成";
	}
	
	@ApiOperation(value = "首页banner")
	@GetMapping("/banners")
	@ResponseBody
	public JsonResult banners(){
		return jsonResultHelper.buildSuccessJsonResult(phBannerService.banners());
	}
	
	@ApiOperation(value = "首页乱七八糟的数据")
	@GetMapping("/data")
	@ResponseBody
	public JsonResult data(){
		Map<String, Object> map = new HashMap<>();
		List<PhStarsameImage> phStarsameImages = phStarsameImageService.indexData();
		map.put("star", phStarsameImages);
		List<PhGoods> phGoods = phGoodsService.indexData();
		map.put("goods", phGoods);
		return jsonResultHelper.buildSuccessJsonResult(map);
	}
	
}
