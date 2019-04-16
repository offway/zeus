package cn.offway.zeus.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;

import cn.offway.zeus.properties.AlipayProperties;

@Service
public class AlipayService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private AlipayClient alipayClient;
	
	@Autowired
	private AlipayProperties alipayProperties;
	
	/** 系统地址 **/
	@Value("${system.url}")
	private String systemUrl;
	
	/**
	 * 生成 APP支付订单信息
	 * @return
	 */
	public AlipayTradeAppPayResponse trade(String outtradeno,String body,String subject,Double amount){
		
		//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		model.setBody(body);
		model.setSubject(subject);
		model.setOutTradeNo(outtradeno);
		model.setTimeoutExpress("30m");
		model.setTotalAmount(""+amount);
		model.setProductCode("QUICK_MSECURITY_PAY");
		request.setBizModel(model);
		request.setNotifyUrl(systemUrl+"/notify/alipay");
		try {
		        //这里和普通的接口调用不同，使用的是sdkExecute
		        AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
		        //就是orderString 可以直接给客户端请求，无需再做处理。
		        logger.info("支付宝生成 APP支付订单信息响应:{}",JSON.toJSONString(response));
		        return response;
		    } catch (AlipayApiException e) {
		        e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 服务端验证异步通知信息参数示例
	 * @param request
	 * @return
	 */
	public boolean validate(HttpServletRequest request){
		try {
			//获取支付宝POST过来反馈信息
			Map<String,String> params = new HashMap<String,String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			    String name = (String) iter.next();
			    String[] values = (String[]) requestParams.get(name);
			    String valueStr = "";
			    for (int i = 0; i < values.length; i++) {
			        valueStr = (i == values.length - 1) ? valueStr + values[i]
			                    : valueStr + values[i] + ",";
			  	}
			    //乱码解决，这段代码在出现乱码时使用。
				//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
				params.put(name, valueStr);
			}
			//切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
			//boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
			boolean flag = AlipaySignature.rsaCheckV1(params, alipayProperties.getPublickey(), "UTF-8","RSA2");
			return flag;
		} catch (AlipayApiException e) {
			e.printStackTrace();
			return false;
		}
	}
}
