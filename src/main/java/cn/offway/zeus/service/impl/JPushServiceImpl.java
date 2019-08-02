package cn.offway.zeus.service.impl;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosAlert;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import cn.jpush.api.schedule.ScheduleResult;
import cn.jpush.api.schedule.model.TriggerPayload;
import cn.offway.zeus.properties.JPushProperties;
import cn.offway.zeus.service.JPushService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JPushServiceImpl implements JPushService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JPushClient jPushClient;
	
	@Autowired
	private JPushProperties jPushProperties;
	
	/**
	 * 极光推送指定用户
	 */
	@Override
	public boolean sendPushUser(String tilte, String alert, Map<String, String> extras,String... alias){
		try {
			PushResult pushResult = jPushClient.sendPush(buildPushUser(tilte, alert, extras,alias));
			String resultJson = JSON.toJSONString(pushResult);
			logger.info("极光推送响应:{}",resultJson);
			boolean result = pushResult.isResultOK();
			if(!result){
				logger.error("极光推送失败,返回:{}",resultJson);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("极光推送异常",e);
			return false;
		}
	}
	
	
	/**
	 * 极光推送-带参数
	 */
	@Override
	public boolean sendPush(String tilte, String alert, Map<String, String> extras){
		try {
			PushResult pushResult = jPushClient.sendPush(buildPushAll(tilte, alert, extras));
			String resultJson = JSON.toJSONString(pushResult);
			logger.info("极光推送响应:{}",resultJson);
			boolean result = pushResult.isResultOK();
			if(!result){
				logger.error("极光推送失败,返回:{}",resultJson);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("极光推送异常",e);
			return false;
		}
	}

	/**
	 * 带参数推送所有用户
	 *
	 */
	private PushPayload buildPushAll(String tilte, String alert, Map<String, String> extras) {

		Notification notification = Notification.newBuilder()
				.addPlatformNotification(
						AndroidNotification.newBuilder().setAlert(alert).setTitle(tilte).addExtras(extras).build())
				.addPlatformNotification(IosNotification.newBuilder()
						.setAlert(IosAlert.newBuilder().setTitleAndBody(tilte, null, alert).build()).addExtras(extras)
						.build())
				.build();

		Audience audience = Audience.alias("7", "8");

		if(jPushProperties.getApnsProduction()){
			audience = Audience.all();
		}
		return PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(audience)
				.setNotification(notification).build();
	}
	
	/**
	 * 带参数推送指定用户
	 *
	 */
	private PushPayload buildPushUser(String tilte, String alert, Map<String, String> extras,String... alias) {

		Notification notification = Notification.newBuilder()
				.addPlatformNotification(
						AndroidNotification.newBuilder().setAlert(alert).setTitle(tilte).addExtras(extras).build())
				.addPlatformNotification(IosNotification.newBuilder()
						.setAlert(IosAlert.newBuilder().setTitleAndBody(tilte, null, alert).build()).addExtras(extras)
						.build())
				.build();

		//开发环境只推送给Mark
		Audience audience = Audience.alias("7", "8");
		
		if(jPushProperties.getApnsProduction()){
			audience = Audience.alias(alias);
		}
		return PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(audience)
				.setNotification(notification).build();
	}

}
