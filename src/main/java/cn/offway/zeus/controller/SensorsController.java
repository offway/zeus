package cn.offway.zeus.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sensorsdata.analytics.javasdk.SensorsAnalytics;
import com.sensorsdata.analytics.javasdk.exceptions.InvalidArgumentException;

import cn.offway.zeus.dto.SaTrack;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;

/**
 * 神策数据相关服务
 * @author wn
 *
 */
@Api(tags={"神策数据"})
@RestController
@RequestMapping("/sensors")
public class SensorsController {

	@Autowired
	private SensorsAnalytics sa;
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 记录事件
	 * @param saTrack 时间内容
	 * @return
	 */
	@PostMapping("/track")
	public JsonResult track(@RequestBody SaTrack saTrack){
		
		try {
			sa.track(saTrack.getDistinctId(), saTrack.isLoginId(), saTrack.getEventName(), saTrack.getProperties());
			return jsonResultHelper.buildSuccessJsonResult(null);
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
			logger.error("神策数据记录事件异常，请求参数:{}",JSON.toJSONString(saTrack,SerializerFeature.WriteMapNullValue),e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
		
	}
	
	/*public static void main(String[] args) {
		SaTrack saTrack = new SaTrack();
		Map<String, Object> properties = new HashMap<>();
		//浏览页面
		saTrack.setDistinctId("unionId的值");
		saTrack.setEventName("event_page_browsing");
		saTrack.setLoginId(false);
		properties = new HashMap<>();
		properties.put("channel", "渠道，可传参[苹果,安卓,公众号,小程序]");
		properties.put("activity_id", "活动id");
		properties.put("activity_name", "活动名称");
		properties.put("page_title", "页面标题");
		properties.put("page_url", "页面地址");
		
		saTrack.setProperties(properties);
		System.out.println(JSON.toJSONString(saTrack,SerializerFeature.WriteMapNullValue));
		
		//分享
		saTrack.setDistinctId("unionId的值");
		saTrack.setEventName("event_share");
		saTrack.setLoginId(false);
		properties = new HashMap<>();
		properties.put("channel", "渠道，可传参[苹果,安卓,公众号,小程序]");
		properties.put("type", "类型，可传参[朋友圈,微信好友]");
		properties.put("activity_id", "活动id");
		properties.put("activity_name", "活动名称");
		properties.put("page_title", "页面标题");
		properties.put("page_url", "页面地址");
		
		saTrack.setProperties(properties);
		System.out.println(JSON.toJSONString(saTrack,SerializerFeature.WriteMapNullValue));
		
		//点击
		saTrack.setDistinctId("unionId的值");
		saTrack.setEventName("event_click");
		saTrack.setLoginId(false);
		properties = new HashMap<>();
		properties.put("channel", "渠道，可传参[苹果,安卓,公众号,小程序]");
		properties.put("activity_id", "活动id");
		properties.put("activity_name", "活动名称");
		properties.put("page_title", "页面标题");
		properties.put("page_url", "页面地址");
		properties.put("element", "点击元素");
		
		
		saTrack.setProperties(properties);
		System.out.println(JSON.toJSONString(saTrack,SerializerFeature.WriteMapNullValue));
		
		
		//奖励
		saTrack.setDistinctId("unionId的值");
		saTrack.setEventName("event_reward");
		saTrack.setLoginId(false);
		properties = new HashMap<>();
		properties.put("channel", "渠道，可传参[苹果,安卓,公众号,小程序]");
		properties.put("activity_id", "活动id");
		properties.put("activity_name", "活动名称");
		properties.put("prize", "奖品，可传参[抽奖码]");
		
		
		saTrack.setProperties(properties);
		System.out.println(JSON.toJSONString(saTrack,SerializerFeature.WriteMapNullValue));
		
	}*/
}
