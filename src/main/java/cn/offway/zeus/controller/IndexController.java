package cn.offway.zeus.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.offway.zeus.domain.PhBanner;
import cn.offway.zeus.domain.PhBrand;
import cn.offway.zeus.domain.PhConfig;
import cn.offway.zeus.domain.PhGoods;
import cn.offway.zeus.domain.PhGoodsCategory;
import cn.offway.zeus.domain.PhGoodsType;
import cn.offway.zeus.domain.PhStarsame;
import cn.offway.zeus.domain.PhWxuserInfo;
import cn.offway.zeus.repository.PhGoodsCategoryRepository;
import cn.offway.zeus.repository.PhGoodsTypeRepository;
import cn.offway.zeus.service.PhBannerService;
import cn.offway.zeus.service.PhBrandService;
import cn.offway.zeus.service.PhConfigService;
import cn.offway.zeus.service.PhGoodsService;
import cn.offway.zeus.service.PhStarsameService;
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
	private PhStarsameService phStarsameService;
	
	@Autowired
	private PhGoodsService phGoodsService;
	
	@Autowired
	private PhBrandService phBrandService;
	
	@Autowired
	private PhConfigService phConfigService;
	
	@Autowired
	private PhGoodsTypeRepository phGoodsTypeRepository;
	
	@Autowired
	private PhGoodsCategoryRepository phGoodsCategoryRepository;

	@ResponseBody
	@GetMapping("/")
	@ApiOperation(value = "欢迎页")
	public String index() {
		return "欢迎访问很潮API服务";
	}

	/**
	 * 微信授权
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


	@ApiOperation(value = "APP初始化")
	@GetMapping("/init")
	@ResponseBody
	public JsonResult init(
			@ApiParam("类型[IOS-苹果,ANDROID-安卓]") @RequestParam String type,
			@ApiParam("版本号") @RequestParam String version){
		Map<String,Object> resultMap = new HashMap<>();
		boolean isUpdateVersion = false;
		String config = phConfigService.findContentByName("UPDATE_"+type);
		if(StringUtils.isNotBlank(config)){
			JSONObject jsonObject = JSON.parseObject(config);
			String confVersion = jsonObject.getString("version");
			int cv = Integer.parseInt(confVersion.trim().replace(".",""));
			int v = Integer.parseInt(version.trim().replace(".",""));
			if(v < cv){
				isUpdateVersion = true;
				resultMap.put("versionConf",jsonObject);
			}
		}
		resultMap.put("isUpdateVersion",isUpdateVersion);
		resultMap.put("ad",phBannerService.banners("2"));
		return jsonResultHelper.buildSuccessJsonResult(resultMap);
	}
	
	@ApiOperation(value = "首页banner")
	@GetMapping("/banners")
	@ResponseBody
	public JsonResult banners(@ApiParam("展示位置[0-首页顶部,1-首页腹部,2-广告页,3-资讯]") @RequestParam(required = false) String position){
		return jsonResultHelper.buildSuccessJsonResult(phBannerService.banners(StringUtils.isBlank(position)?"0":position));
	}
	
	@ApiOperation(value = "首页乱七八糟的数据")
	@GetMapping("/data")
	@ResponseBody
	public JsonResult data(){
		Map<String, Object> map = new HashMap<>();
		List<PhStarsame> phStarsames = phStarsameService.indexData();
		PhStarsame phStarsame = new PhStarsame();
		phStarsame.setImageUrl("http://qiniu.offway.cn/image/f519e936b5a14339800e0862440ba72d%20.jpg");
		phStarsames.add(phStarsame);
		map.put("star", phStarsames);
		List<PhBrand> brands = phBrandService.findByIsRecommendOrderBySortAsc("1");
		map.put("brands", brands);
		List<PhGoods> phGoods = phGoodsService.indexData();
		map.put("goods", phGoods);
		String config = phConfigService.findContentByName("INDEX_CATEGORY");
		map.put("categories", JSON.parse(config));
		
		return jsonResultHelper.buildSuccessJsonResult(map);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value = "首页乱七八糟的数据")
	@GetMapping("/data/v2")
	@ResponseBody
	public JsonResult datav2(){
		Map<String, Object> map = new HashMap<>();
		
		map.put("star", phStarsameService.indexData());
		
		List<PhBanner> phBanners = phBannerService.banners();
		
		List<PhBanner> banners = new ArrayList<>();
		List<PhBanner> promoteSales = new ArrayList<>();
		for (PhBanner phBanner : phBanners) {
			if("0".equals(phBanner.getPosition())){
				banners.add(phBanner);
			}else if("1".equals(phBanner.getPosition())){
				promoteSales.add(phBanner);
			}
		}
		
		map.put("banners", banners);
		map.put("promoteSales", promoteSales);

		List<PhConfig> configs = phConfigService.findByNameIn("INDEX_CATEGORY_IMG","INDEX_IMAGES","INDEX_IMAGES_2","INDEX_BRAND_LOGO","INDEX_BRAND_GOODS","INDEX_CATEGORY","INDEX_STYLE","INDEX_DISCOUNT","INDEX_SELL_WELL");
		for (PhConfig phConfig : configs) {
			String name = phConfig.getName().toLowerCase();
			String content = phConfig.getContent();
			if("index_brand_goods".equals(name)){
				List<Map> brands  = JSON.parseArray(content,Map.class);
				for (Map<String,Object> brand : brands) {
					Long brandId = Long.parseLong(brand.get("id").toString());
					brand.put("goods", phGoodsService.findBrandRecommend(brandId));
				}
				map.put(name,brands);
			}else{
				map.put(name,JSON.parse(content));
			}
		}
		
		return jsonResultHelper.buildSuccessJsonResult(map);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value = "首页数据-小程序")
	@GetMapping("/data/mini")
	@ResponseBody
	public JsonResult datavMini(){
		Map<String, Object> map = new HashMap<>();

		map.put("banners", phBannerService.banners("4"));

		List<PhConfig> configs = phConfigService.findByNameIn("INDEX_IMAGES_MINI","INDEX_BRAND_GOODS_MINI","INDEX_CATEGORY_MINI");
		for (PhConfig phConfig : configs) {
			String name = phConfig.getName().toLowerCase();
			String content = phConfig.getContent();
			if("index_brand_goods_mini".equals(name)){
				List<Map> brands  = JSON.parseArray(content,Map.class);
				for (Map<String,Object> brand : brands) {
					Long brandId = Long.parseLong(brand.get("id").toString());
					brand.put("goods", phGoodsService.findBrandRecommend(brandId));
				}
				map.put(name,brands);
			}else{
				map.put(name,JSON.parse(content));
			}
		}

		return jsonResultHelper.buildSuccessJsonResult(map);
	}
	
	@ApiOperation("搜索")
	@GetMapping("/search")
	@ResponseBody
	public JsonResult search(@ApiParam("搜索关键字") @RequestParam String wd){
		
		
		List<String> s = new ArrayList<>();
		List<Map<String, Object>> list = new ArrayList<>();
		//匹配品牌
		List<PhBrand> brands = phBrandService.findByNameLike("%"+wd+"%");
		for (PhBrand phBrand : brands) {
			Map<String, Object> map = new HashMap<>();
			map.put("type", "brand");
			map.put("value", phBrand.getName());
			map.put("info", phBrand);
			list.add(map);
			s.add(phBrand.getName());
		}
		//匹配商品分类
		List<String> types = phGoodsTypeRepository.findByNameLike("%"+wd+"%");
		for (String type : types) {
			Map<String, Object> map = new HashMap<>();
			map.put("type", "type");
			map.put("value", type);
			map.put("info", null);
			list.add(map);
			s.add(type);
		}
		//匹配商品二级分类
		List<String> categories = phGoodsCategoryRepository.findByNameLike("%"+wd+"%");
		for (String category : categories) {
			Map<String, Object> map = new HashMap<>();
			map.put("type", "category");
			map.put("value", category);
			map.put("info", null);
			list.add(map);
			s.add(category);
		}
		
		return jsonResultHelper.buildSuccessJsonResult(s);
	}
	
	@ApiOperation("搜索")
	@GetMapping("/search/v2")
	@ResponseBody
	public JsonResult searchv2(@ApiParam("搜索关键字") @RequestParam String wd){
		
		List<String> s = new ArrayList<>();
		//匹配品牌
		List<String> brands = phBrandService.findNameLike("%"+wd+"%");
		int size = brands.size();
		if(size>1){
			s = brands;
		}else if(size==1){
			String name = brands.get(0);
			s.add(name);
			List<String> c = phGoodsService.searchCategory(name);
			s.addAll(c);
		}
		
		return jsonResultHelper.buildSuccessJsonResult(s);
	}
}
