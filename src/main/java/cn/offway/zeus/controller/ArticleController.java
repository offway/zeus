package cn.offway.zeus.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import cn.offway.zeus.domain.PhGoods;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import cn.offway.zeus.domain.PhArticle;
import cn.offway.zeus.dto.ArticleDto;
import cn.offway.zeus.service.PhArticleService;
import cn.offway.zeus.service.PhConfigService;
import cn.offway.zeus.service.PhGoodsService;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags={"文章"})
@RestController
@RequestMapping("/article")
public class ArticleController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhArticleService phArticleService;
	
	@Autowired
	private PhConfigService phConfigService;
	
	@Autowired
	private PhGoodsService phGoodsService;
	
	@ApiOperation("列表")
	@PostMapping("/list")
	public JsonResult list(@ApiParam("请求参数") @RequestBody ArticleDto articleDto){
		Page<PhArticle> page =  phArticleService.findByPage(articleDto, PageRequest.of(articleDto.getPage(), articleDto.getSize()));
		return jsonResultHelper.buildSuccessJsonResult(page);
	}
	
	@ApiOperation("配置")
	@GetMapping("/config")
	public JsonResult config(){
		String content = phConfigService.findContentByName("ARTICLE_NAV");
		return jsonResultHelper.buildSuccessJsonResult(JSON.parseArray(content));
	}
	
	@ApiOperation("文章推荐商品")
	@GetMapping("/info")
	public JsonResult info(@ApiParam("文章ID") @RequestParam Long id){
		Map<String, Object> map = new HashMap<>();
		PhArticle phArticle = phArticleService.findById(id);
		map.put("article", phArticle);
		String goodsIds = phArticle.getGoodsIds();
		if(StringUtils.isNotBlank(goodsIds)){
			String[] ids = goodsIds.split(",");
			if(ids.length >0){
				map.put("goods", phGoodsService.findByIds(Arrays.asList(ids)));
			}
		}

		int size = 3;//推荐3条
		List<PhArticle> recommends = phArticleService.findRecommendByTypeAndTag(phArticle.getType(),size,id, phArticle.getTag());
		int s = size - recommends.size();
		if(s>0){
			List<PhArticle> recommends1 = phArticleService.findRecommendByType(phArticle.getType(),s,id);
			recommends.addAll(recommends1);
		}
		map.put("recommends", recommends);

		return jsonResultHelper.buildSuccessJsonResult(map);
	}

	@ApiOperation("推荐列表")
	@GetMapping("/recommend")
	public JsonResult recommend(){
		List<PhArticle> news = phArticleService.findRecommendByType("0",32,0L);
		List<PhArticle> ss = phArticleService.findRecommendByType("1",32,0L);
		List<PhArticle> videos = phArticleService.findRecommendByType("2",32,0L);
		List<PhArticle> list = new ArrayList<>();
		
		int count = news.size()+ss.size()+videos.size();
		int n = 0;
		int s = 0;
		int v = 0;
		int l = 32;
		do {
			if(news.size()-1>=n) list.add(news.get(n));
			if(list.size()==count||list.size()==l) break;
			n++;
			if(news.size()-1>=n) list.add(news.get(n));
			if(list.size()==count||list.size()==l) break;
			n++;
			if(news.size()-1>=n) list.add(news.get(n));
			if(list.size()==count||list.size()==l) break;
			n++;
			
			if(ss.size()-1>=s) list.add(ss.get(s));
			if(list.size()==count||list.size()==l) break;
			s++;
			
			if(news.size()-1>=n) list.add(news.get(n));
			if(list.size()==count||list.size()==l) break;
			n++;
			if(news.size()-1>=n) list.add(news.get(n));
			if(list.size()==count||list.size()==l) break;
			n++;
			if(news.size()-1>=n) list.add(news.get(n));
			if(list.size()==count||list.size()==l) break;
			n++;
			
			if(videos.size()-1>=v) list.add(videos.get(v));
			if(list.size()==count||list.size()==l) break;
			v++;
		} while (true);
		
		
		
		return jsonResultHelper.buildSuccessJsonResult(list);
	}
	
	@ApiOperation("详情页")
	@GetMapping(value = "/{type}/{id}.html",produces="text/html;charset=UTF-8")
	public String html(
			@ApiParam("类型[info-详情页,share-分享页]") @PathVariable String type,
			@ApiParam("文章ID") @PathVariable Long id){
		String html = "<!DOCTYPE html>\n<!--[if lt IE 7 ]> <html class=\"ie ie6\"> <![endif]-->\n<!--[if IE 7 ]>    <html class=\"ie ie7\"> <![endif]-->\n<!--[if IE 8 ]>    <html class=\"ie ie8\"> <![endif]-->\n<!--[if IE 9 ]>    <html class=\"ie ie9\"> <![endif]-->\n<!--[if (gt IE 9)|!(IE)]><!--> <html lang=\"zh-cn\"> <!--<![endif]-->\n<head lang=\"en\">\n    <meta charset=\"UTF-8\">\n    <meta name=\"viewport\" content=\"width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no\" />\n    <title>###NAME###</title>\n    <link rel=\"stylesheet\" href=\"https://api.offway.cn:8443/wxshare/style/style.css\">\n    <script src=\"https://api.offway.cn:8443/js/jquery.min.js\"></script>\n    <script src=\"https://api.offway.cn:8443/wxshare/script/jquery-m.js\"></script>\n    <style>\n		.p-box img {\n		    margin: auto auto !important;\n		}\n		.page-3 {\n		    border-bottom: 10px solid #fff;\n		}\n	</style>\n</head>\n<body>\n<div class=\"page-3\">\n    <div class=\"title\">###TITLE###</div>\n    <div class=\"span\">\n        <span class=\"tip\">###TYPE###</span>\n        <span class=\"eye\">###VIEWCOUNT###</span>\n        <span class=\"time\">###CREATETIME###</span>\n    </div>\n    <div class=\"p-box\">\n###CONTENT### \n ###RECOMMEND_GOODS###\n    </div>\n</div>\n<div class=\"foot-none\"></div>\n<div class=\"foot-btn\"><a href=\"https://android.myapp.com/myapp/detail.htm?apkName=com.puhao.offway\">下载APP</a> </div>\n</body>\n\n<script src=\"https://res.wx.qq.com/open/js/jweixin-1.4.0.js\"></script>\n<script src=\"https://api.offway.cn:8443/js/jquery.min.js\"></script>\n<script type=\"text/javascript\">\nfunction goWxShare(shareOption){	\n	var imgurl =  shareOption.imgurl;\n	\n	var url = shareOption.fromUrl ;\n	var forumid=\"\";\n	\n		\n	if(null===url || ''===url || undefined===url){\n		url = location.href ;\n	}\n	if(url.indexOf(\"forumid=\")>=0){\n		forumid=shareOption.forumid;		\n	}\n\n	\n	$.ajax({  \n	    url:\"https://zeus.offway.cn/access/wx/config\",  \n	    data: {url:url}, \n		dataType: 'json',\n	    type:'post',\n	    error: function(data) {   \n	        // alert(JSON.stringify(data));\n	    },  \n	    success:function(data){\n	    	// alert(JSON.stringify(data));\n	    	//var temp = JSON.parse(data);\n	    	if(null!==data ){\n		    		//通过config接口注入权限验证配置\n					wx.config({\n					  debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。\n					  appId: data.appid, // 必填，公众号的唯一标识\n					  timestamp: data.timestamp, // 必填，生成签名的时间戳\n					  nonceStr: data.nonce, // 必填，生成签名的随机串\n					  signature: data.getSHA1,// 必填，签名，见附录\n					  jsApiList: ['onMenuShareTimeline', 'onMenuShareAppMessage','hideMenuItems'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2\n					});\n			    	\n		    		// 更改七牛分享的尺寸大小\n		    		// var imgurl =  shareOption.imgurl;\n					\n					// ready 接口处理成功\n					wx.ready(function(){\n						//分享给朋友\n						//title:分享的标题；link分享的链接；imgUrl分享的图标\n					 	wx.onMenuShareAppMessage({\n					 	    title: shareOption.title,\n					 	    desc: shareOption.desc,\n					 	    link: url,\n					 	    imgUrl:  imgurl,\n					 	    success: function () {\n					 	    	//getShareConfig(); \n					 	    	// shareTask(1,forumid);\n					 	    }\n					 	});\n\n						//分享给朋友圈\n						//title:分享的标题；link分享的链接；imgUrl分享的图标\n					 	wx.onMenuShareTimeline({\n					 		title: shareOption.title , \n					 		link:  url,\n					 	    imgUrl:  imgurl,\n					 	    success: function () {\n					 	    	//getShareConfig();\n					 	    	// shareTask(2,forumid);\n					 	    }\n					    });\n					 	//============\n					}); 			\n		    	\n		    	}\n	    	//=================\n	    }\n	});\n}\n</script>\n\n<script type=\"text/javascript\">\ngoWxShare({\n	imgurl:'###IMAGE###',\n	title:'###NAME###',\n	desc:'###TITLE###',\n	fromUrl:location.href\n});\n</script>\n</html>";
		PhArticle phArticle = phArticleService.findById(id);
		phArticle.setViewCount(phArticle.getViewCount()+1L);
		phArticle = phArticleService.save(phArticle);
		html = html.replaceAll("###TITLE###", phArticle.getTitle());
		html = html.replaceAll("###TYPE###", phArticle.getTag());
		html = html.replaceAll("###VIEWCOUNT###", phArticle.getViewCount()+"");
		Date date = phArticle.getApproval();
		html = html.replaceAll("###CREATETIME###", DateFormatUtils.format(null==date?phArticle.getCreateTime():date, "yyyy-MM-dd"));
		String content = phArticle.getContent();
		content = content.replaceAll("\\$", "###RDS_CHAR_DOLLAR###");// encode $;
		html = html.replaceAll("###CONTENT###", content);
		html = html.replaceAll("###RDS_CHAR_DOLLAR###", "\\$");// decode $

		html = html.replaceAll("###IMAGE###", phArticle.getImage());
		html = html.replaceAll("###NAME###", phArticle.getName());
		
		if("info".equals(type)){
			html = html.replaceAll("<\\s*?script[^>]*?>[\\s\\S]*?<\\s*?/\\s*?script\\s*?>", ""); 
			html = html.replaceAll("foot-btn", "undis"); 
			html = html.replaceAll("foot-none", "undis"); 
		}else{
			String re = "";
			String goodsIds = phArticle.getGoodsIds();
			if(StringUtils.isNotBlank(goodsIds)){
				String[] ids = goodsIds.split(",");
				if(ids.length >0){
					 List<PhGoods> goodss = phGoodsService.findByIds(Arrays.asList(ids));
					for (PhGoods goods : goodss) {
						re += "<div style=\"width: 6.75rem;height: 1.9rem;box-shadow: 0 0 0.1rem 0 rgba(127,127,127,0.50);margin: .1rem .0 .1rem 0.04rem;display: flex;align-items: center;\" onclick=\"window.location.href='https://sj.qq.com/myapp/detail.htm?apkName=com.puhao.offway'\">\n" +
								"  <div style=\"width: 1.4rem;height: 1.4rem;margin: .2rem .2rem auto;\">\n" +
								"  <img style=\"width: 100%;height: 100%;\" src=\""+goods.getImage()+"\"></div>\n" +
								"  <div style=\"width: 3.8rem;font-size: .24rem;text-align: left;line-height: .3rem\">"+goods.getName()+"</div>\n" +
								"  <div style=\"width: .02rem;height: 1rem;background: #F4F4F4;margin-left: .2rem;\"></div>\n" +
								"  <div style=\"width: .45rem;height: .45rem;font-size: 0;margin-left: .25rem;\">\n" +
								"  <img style=\"width: 100%;height: 100%;\" src=\"http://qiniu.offway.cn/image/wx/50/48/e04f9904-5d31-01e9-7089-b9dba5897f6e.png\"></div>\n" +
								"</div>";
					}
				}
			}


			html = html.replaceAll("###RECOMMEND_GOODS###", re);
		}
		return html;
	}
	
}
