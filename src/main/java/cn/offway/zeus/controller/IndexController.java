package cn.offway.zeus.controller;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.offway.zeus.domain.PhWxuserInfo;
import cn.offway.zeus.service.PhWxuserInfoService;
import cn.offway.zeus.utils.HttpClientUtil;
import cn.offway.zeus.utils.JsonResultHelper;
import cn.offway.zeus.utils.WechatUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 首页
 * @author wn
 *
 */
@RestController
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
			phWxuserInfo.setAppDownload("0");//默认没下载APP
			phWxuserInfo.setCreateTime(new Date());
			if(null != oldphWxuserInfo){
				phWxuserInfo.setId(oldphWxuserInfo.getId());
				phWxuserInfo.setAppDownload(oldphWxuserInfo.getAppDownload());
			}
			return phWxuserInfoService.save(phWxuserInfo);
		}else{
			return JSON.parseObject(userInfoStr);
		}
	}

	@ApiOperation(value = "微信授权-获取JS-SDK配置")
	@PostMapping("/access/wx/config")
	public Map<String, String> getJssdkinfo(String url) {
		return WechatUtil.getJssdkinfo(url, APPID, SECRET);
	}
	
//	public static void main(String[] args) throws InvalidArgumentException {
//		// 从神策分析获取的数据接收的 URL
//		final String SA_SERVER_URL = "http://101.132.142.203:8106/sa?project=default";
//		// 使用 Debug 模式，并且导入 Debug 模式下所发送的数据
//		final boolean SA_WRITE_DATA = true;
//
//		// 使用 DebugConsumer 初始化 SensorsAnalytics
//		final SensorsAnalytics sa = new SensorsAnalytics(
//				new SensorsAnalytics.DebugConsumer(SA_SERVER_URL, SA_WRITE_DATA));
//
//		// 用户的 Distinct Id
//		String distinctId = "vinann";
//		
//		Map<String, Object> properties = new HashMap<String, Object>();
//
//		// '$time' 属性是系统预置属性，表示事件发生的时间，如果不填入该属性，则默认使用系统当前时间
//		properties.put("$time", new Date());
//		// '$ip' 属性是系统预置属性，如果服务端中能获取用户 IP 地址，并填入该属性，神策分析会自动根据 IP 地址解析用户的省份、城市信息
//		properties.put("$ip", "101.132.142.203");
//		// 商品 ID
//		properties.put("ProductId", "123456");
//		// 商品类别
//		properties.put("ProductCatalog", "iPhone");
//		// 是否加入收藏夹，Boolean 类型的属性
//		properties.put("isAddedToFav", true);
//
//		// 记录用户浏览商品事件
//		sa.track(distinctId, false, "ViewProduct", properties);
//
//		// 使用神策分析记录用户行为数据
//		// ...
//
//		// 程序结束前，停止神策分析 SD
//		sa.shutdown();
//	}
}
