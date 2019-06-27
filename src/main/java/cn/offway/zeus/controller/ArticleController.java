package cn.offway.zeus.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import cn.offway.zeus.enums.ArticleTypeEnum;
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
		Page<PhArticle> page =  phArticleService.findByPage(articleDto, new PageRequest(articleDto.getPage(), articleDto.getSize()));
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
		PhArticle phArticle = phArticleService.findOne(id);
//		map.put("article", phArticle);
		String goodsIds = phArticle.getGoodsIds();
		if(StringUtils.isNotBlank(goodsIds)){
			String[] ids = goodsIds.split(",");
			if(ids.length >0){
				map.put("goods", phGoodsService.findByIds(Arrays.asList(ids)));
			}
		}
		List<PhArticle> recommends = phArticleService.findRecommendByType(phArticle.getType(),3,id);
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
		String html = "<!DOCTYPE html><!--[if lt IE 7 ]> <html class=\"ie ie6\"> <![endif]--><!--[if IE 7 ]>    <html class=\"ie ie7\"> <![endif]--><!--[if IE 8 ]>    <html class=\"ie ie8\"> <![endif]--><!--[if IE 9 ]>    <html class=\"ie ie9\"> <![endif]--><!--[if (gt IE 9)|!(IE)]><!--> <html lang=\"zh-cn\"> <!--<![endif]--><head lang=\"en\">    <meta charset=\"UTF-8\">    <meta name=\"viewport\" content=\"width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no\" />    <title>###NAME###</title>    <link rel=\"stylesheet\" href=\"http://api.myoffway.com/wxshare/style/style.css\">    <script src=\"http://api.myoffway.com/js/jquery.min.js\"></script>    <script src=\"http://api.myoffway.com/wxshare/script/jquery-m.js\"></script>    <style>		.p-box img {		    margin: auto auto !important;		}		.page-3 {		    border-bottom: 10px solid #fff;		}	</style></head><body><div class=\"page-3\">    <div class=\"title\">###TITLE###</div>    <div class=\"span\">        <span class=\"tip\">###TYPE###</span>        <span class=\"eye\">###VIEWCOUNT###</span>        <span class=\"time\">###CREATETIME###</span>    </div>    <div class=\"p-box\">###CONTENT###    </div></div><div class=\"foot-none\"></div><div class=\"foot-btn\"><a href=\"https://android.myapp.com/myapp/detail.htm?apkName=com.puhao.offway\">下载APP</a> </div></body><script src=\"http://res.wx.qq.com/open/js/jweixin-1.4.0.js\"></script><script src=\"http://api.myoffway.com/js/jquery.min.js\"></script><script type=\"text/javascript\">function goWxShare(shareOption){		var imgurl =  shareOption.imgurl;		var url = shareOption.fromUrl ;	var forumid=\"\";				if(null===url || ''===url || undefined===url){		url = location.href ;	}	if(url.indexOf(\"forumid=\")>=0){		forumid=shareOption.forumid;			}		$.ajax({  	    url:\"https://zeus.offway.cn/access/wx/config\",  	    data: {url:url}, 		dataType: 'json',	    type:'post',	    error: function(data) {   	        // alert(JSON.stringify(data));	    },  	    success:function(data){	    	// alert(JSON.stringify(data));	    	//var temp = JSON.parse(data);	    	if(null!==data ){	    			    		//通过config接口注入权限验证配置					wx.config({					  debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。					  appId: data.appid, // 必填，公众号的唯一标识					  timestamp: data.timestamp, // 必填，生成签名的时间戳					  nonceStr: data.nonceStr, // 必填，生成签名的随机串					  signature: data.signature,// 必填，签名，见附录1					  jsApiList: ['onMenuShareTimeline', 'onMenuShareAppMessage','hideMenuItems'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2					});			    			    		// 更改七牛分享的尺寸大小		    		// var imgurl =  shareOption.imgurl;										// ready 接口处理成功					wx.ready(function(){						//分享给朋友						//title:分享的标题；link分享的链接；imgUrl分享的图标					 	wx.onMenuShareAppMessage({					 	    title: shareOption.title,					 	    desc: shareOption.desc,					 	    link: url,					 	    imgUrl:  imgurl,					 	    success: function () {					 	    	//getShareConfig(); 					 	    	// shareTask(1,forumid);					 	    }					 	});						//分享给朋友圈						//title:分享的标题；link分享的链接；imgUrl分享的图标					 	wx.onMenuShareTimeline({					 		title: shareOption.title , 					 		link:  url,					 	    imgUrl:  imgurl,					 	    success: function () {					 	    	//getShareConfig();					 	    	// shareTask(2,forumid);					 	    }					    });					 	//============					}); 					    			    	}	    	//=================	    }	});}</script><script type=\"text/javascript\">goWxShare({	imgurl:'###IMAGE###',	title:'###NAME###',	desc:'###TITLE###,	fromUrl:location.href});</script></html>";
		PhArticle phArticle = phArticleService.findOne(id);
		html = html.replaceAll("###TITLE###", phArticle.getTitle());
		html = html.replaceAll("###TYPE###", ArticleTypeEnum.getByCode(phArticle.getType()).getDesc());
		html = html.replaceAll("###VIEWCOUNT###", phArticle.getViewCount()+"");
		html = html.replaceAll("###CREATETIME###", DateFormatUtils.format(phArticle.getApproval(), "yyyy-MM-dd"));
		html = html.replaceAll("###CONTENT###", phArticle.getContent());
		html = html.replaceAll("###IMAGE###", phArticle.getImage());
		html = html.replaceAll("###NAME###", phArticle.getName());
		
		if("info".equals(type)){
			html = html.replaceAll("<\\s*?script[^>]*?>[\\s\\S]*?<\\s*?/\\s*?script\\s*?>", ""); 
			html = html.replaceAll("foot-btn", "undis"); 
			html = html.replaceAll("foot-none", "undis"); 
		}
		return html;
	}
	
}
