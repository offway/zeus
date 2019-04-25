package cn.offway.zeus.service;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsBatchSend;
import com.yunpian.sdk.model.SmsSingleSend;

import cn.offway.zeus.domain.PhSmsInfo;

@Service
public class SmsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private YunpianClient yunpianClient;
	
	@Value("${is-prd}")
	private boolean isPrd;
	
	@Autowired
	private PhSmsInfoService phSmsInfoService;
	
	/**
	 * 发送短信
	 * @param phone
	 * @param message
	 * @param ip
	 * @return
	 */
	public boolean sendMsg(String phone,String message,String ip){
		
		boolean result = false;
		PhSmsInfo phSmsInfo = new PhSmsInfo();
		phSmsInfo.setCreateTime(new Date());
		phSmsInfo.setMessage(message);
		phSmsInfo.setPhone(phone);
		phSmsInfo.setIp(ip);
		
		if(isPrd){
			//检查是否超过限制
			int count = phSmsInfoService.countByIp(ip);
			if(count<10){
				Map<String, String> param = yunpianClient.newParam(2);
				param.put(YunpianClient.MOBILE, phone);
				param.put(YunpianClient.TEXT, message);
				
				logger.info("短信发送请求:"+JSON.toJSONString(param));
				Result<SmsSingleSend> r = yunpianClient.sms().single_send(param);
				logger.info("短信发送响应:"+JSON.toJSONString(r));
				
				phSmsInfo.setRetCode(""+r.getCode());
				phSmsInfo.setRetMsg(r.getMsg());
				if(0 == r.getCode()){
					result = true;
				}
			}
		}else{
			result = true;
		}
		phSmsInfoService.save(phSmsInfo);
		return result;
	}
	
	/**
	 * 发送短信批量
	 * @param phone 多个,相隔
	 * @param message
	 * @param ip
	 * @return
	 */
	public boolean sendMsgBatch(String phone,String message){
		
		boolean result = false;
		if(isPrd){
			Map<String, String> param = yunpianClient.newParam(2);
			param.put(YunpianClient.MOBILE, phone);
			param.put(YunpianClient.TEXT, message);
			
			logger.info("短信批量发送请求:"+JSON.toJSONString(param));
			Result<SmsBatchSend> r = yunpianClient.sms().batch_send(param);
			logger.info("短信批量发送响应:"+JSON.toJSONString(r));
			if(0 == r.getCode()){
				result = true;
			}
		}else{
			result = true;
		}
		return result;
	}
}
