package cn.offway.zeus.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jpay.ext.kit.PaymentKit;
import com.jpay.weixin.api.WxPayApi;

import cn.offway.zeus.properties.WxpayProperties;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;

@Service
public class WxpayService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@Autowired
	private WxpayProperties wxpayProperties;
	
	/** 系统地址 **/
	@Value("${system.url}")
	private String systemUrl;
	
	@Autowired
	private JsonResultHelper jsonResultHelper;

	/**
	 *
	 * @param outtradeno
	 * @param ip
	 * @param body
	 * @param amount
	 * @param type 0-APP,1-MINI
	 * @param openid
	 * @return
	 */
	public JsonResult trade(String outtradeno,String ip,String body,double amount,String type,String openid){
		
		try {

			String mchId = wxpayProperties.getMchId();
			// 支付金额 **金额不能有小数点,单位是分!!**
			BigDecimal price = new BigDecimal(amount);
			BigDecimal beishu = new BigDecimal("100");
			BigDecimal priceFee = price.multiply(beishu);
			// 创建 时间戳
			String timeStamp = Long.valueOf(System.currentTimeMillis()/1000).toString();
			
			// 创建hashmap(用户获得签名)
			SortedMap<String, String> paraMap = new TreeMap<String, String>();

			// 设置请求参数(商户号)
			paraMap.put("mch_id", mchId);
			
			// 生成32位随机数
			UUID uuid = UUID.randomUUID();
			String nonceStr = uuid.toString().replaceAll("-", "");
			// 设置请求参数(随机字符串)
			paraMap.put("nonce_str", nonceStr);
			// 设置请求参数(商品描述)
			paraMap.put("body", body);
			// 设置请求参数(商户订单号)
			paraMap.put("out_trade_no", outtradeno);
			// 设置请求参数(总金额)
			paraMap.put("total_fee", priceFee.toBigInteger().toString());
			// 设置请求参数(终端IP) 如果是springmvc,或者能获取到request的servlet,用下面这种
			paraMap.put("spbill_create_ip", ip);
			// 设置请求参数(通知地址)
			paraMap.put("notify_url", systemUrl+"/notify/wxpay");
			// 设置请求参数(交易类型)
			if("1".equals(type)){
				paraMap.put("openid", openid);
				// 设置请求参数(小程序ID)
				paraMap.put("appid", wxpayProperties.getMiniAppid());
				paraMap.put("trade_type", String.valueOf(WxPayApi.TradeType.JSAPI));

			}else{
				// 设置请求参数(小程序ID)
				paraMap.put("appid", wxpayProperties.getAppid());
				paraMap.put("trade_type", String.valueOf(WxPayApi.TradeType.APP));
			}

			// MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
			String sign = PaymentKit.createSign(paraMap, wxpayProperties.getPaternerKey());
			paraMap.put("sign", sign);
			// 统一下单,向微信api发送数据
			logger.info("微信支付统一下单发送的数据: " + paraMap.toString());
			String xmlResult = WxPayApi.pushOrder(false, paraMap);
			logger.info("微信支付统一下单接受返回的结果: " + xmlResult);
			// 转成xml
			Map<String, String> map = PaymentKit.xmlToMap(xmlResult);
			// 返回状态码
			String return_code =  map.get("return_code");
			// 返回给小程序端需要的参数
			Map<String, String> returnMap = new HashMap<String, String>();
			if ("SUCCESS".equals(return_code)) {
				// 返回的预付单信息

				String prepay_id =  map.get("prepay_id");
				if("1".equals(type)){
					returnMap.put("appId", wxpayProperties.getMiniAppid());
					returnMap.put("nonceStr", nonceStr);
					returnMap.put("package", "prepay_id=" + prepay_id);
					returnMap.put("signType", "MD5");
					returnMap.put("timeStamp", timeStamp);
					String paySign = PaymentKit.createSign(returnMap, wxpayProperties.getPaternerKey()).toUpperCase();
					returnMap.put("paySign", paySign);
				}else{
					returnMap.put("appid", wxpayProperties.getAppid());
					returnMap.put("noncestr", nonceStr);
					returnMap.put("package", "Sign=WXPay");
					returnMap.put("prepayid", prepay_id);
					returnMap.put("partnerid", mchId);
					// 这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
					returnMap.put("timestamp", timeStamp);
					// 拼接签名需要的参数
					// 再次签名，这个签名用于小程序端调用wx.requesetPayment方法
					String paySign = PaymentKit.createSign(returnMap, wxpayProperties.getPaternerKey()).toUpperCase();
					returnMap.put("sign", paySign);
				}
				return jsonResultHelper.buildSuccessJsonResult(returnMap);
			} else {
				logger.error("微信支付下单失败，返回:{}",xmlResult);
				return new JsonResult(return_code, getMsgByCode(return_code));
			}
		} catch (Exception e) {
			logger.error("微信支付统一下单异常,订单号：{} ",outtradeno, e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
	 /**
     * 判断返回的return_code,给前端相应的提示
     *
     */
    private String getMsgByCode(String return_code) {
        switch (return_code){
            case "NOTENOUGH":return "您的账户余额不足";
            case "ORDERPAID":return "该订单已支付完成,请勿重复支付";
            case "ORDERCLOSED":return "当前订单已关闭，请重新下单";
            case "SYSTEMERROR":return "系统超时，请重新支付";
            case "OUT_TRADE_NO_USED":return "请勿重复提交该订单";
            default:return  "网络正在开小差,请稍后再试";
        }
    }
    
}
