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
	 * 统一下单 -小程序JSAPI
	 * @param outtradeno
	 * @param ip
	 * @param body
	 * @param amount
	 * @param openid
	 * @return
	 */
	public JsonResult trade_JSAPI(String outtradeno,String ip,String body,double amount,String openid){
		
		try {

			if(wxpayProperties.getIsSandbox()) amount = 1.01D;
			// 支付金额 **金额不能有小数点,单位是分!!**
			BigDecimal price = new BigDecimal(amount);
			BigDecimal beishu = new BigDecimal("100");
			BigDecimal priceFee = price.multiply(beishu);

			// 创建 时间戳
			String timeStamp = Long.valueOf(System.currentTimeMillis()/1000).toString();
			
			// 创建hashmap(用户获得签名)
			SortedMap<String, String> paraMap = new TreeMap<String, String>();

			String mchId = wxpayProperties.getMchId();
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
			paraMap.put("openid", openid);
			// 设置请求参数(小程序ID)
			paraMap.put("appid", "wxa524a03865079836");
			paraMap.put("trade_type", "JSAPI");

			// MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
			String sign = PaymentKit.createSign(paraMap, wxpayProperties.getPaternerKey());
			paraMap.put("sign", sign);
			// 统一下单,向微信api发送数据
			logger.info("微信支付统一下单发送的数据: " + paraMap.toString());
			String xmlResult = WxPayApi.pushOrder(wxpayProperties.getIsSandbox(), paraMap);
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
				returnMap.put("appId", "wxa524a03865079836");
				returnMap.put("nonceStr", nonceStr);
				returnMap.put("package", "prepay_id=" + prepay_id);
				returnMap.put("signType", "MD5");
				returnMap.put("timeStamp", timeStamp);
				String paySign = PaymentKit.createSign(returnMap, wxpayProperties.getPaternerKey()).toUpperCase();
				returnMap.put("paySign", paySign);
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
	 * 统一下单-APP
	 * @param outtradeno
	 * @param ip
	 * @param body
	 * @param amount
	 * @return
	 */
	public JsonResult trade_APP(String outtradeno,String ip,String body,double amount){

		try {

			if(wxpayProperties.getIsSandbox()){
				amount = 1.01D;
			}
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
			paraMap.put("appid", "wx6d864009d94d2cd7");
			paraMap.put("trade_type", "APP");

			// MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
			String sign = PaymentKit.createSign(paraMap, wxpayProperties.getPaternerKey());
			paraMap.put("sign", sign);
			// 统一下单,向微信api发送数据
			logger.info("微信支付统一下单发送的数据: " + paraMap.toString());
			String xmlResult = WxPayApi.pushOrder(wxpayProperties.getIsSandbox(), paraMap);
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

				returnMap.put("appid", "wx6d864009d94d2cd7");
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
	 * 统一下单 H5
	 * @param outtradeno
	 * @param ip
	 * @param body
	 * @param amount
	 * @return
	 */
	public JsonResult trade_MWEB(String outtradeno,String ip,String body,double amount,String wapUrl,String wapName){

		try {

			// 创建hashmap(用户获得签名)
			SortedMap<String, String> paraMap = new TreeMap<String, String>();

			paraMap.put("appid","wx22e5d09caef77790");//yuzhaojian@puhao-art.com	公众账号ID	是	String(32)	wxd678efh567hg6787	微信分配的公众账号ID（企业号corpid即为此appId）

			String mchId = wxpayProperties.getMchId();
			paraMap.put("mch_id",mchId);//	商户号	是	String(32)	1230000109	微信支付分配的商户号
			paraMap.put("device_info","WEB");//	设备号	否	String(32)	1.3467E+13	终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"

			// 生成32位随机数
			UUID uuid = UUID.randomUUID();
			String nonceStr = uuid.toString().replaceAll("-", "");
			paraMap.put("nonce_str",nonceStr);//	随机字符串	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，不长于32位。推荐随机数生成算法
			paraMap.put("body",body);//	商品描述	是	String(128)	腾讯充值中心-QQ会员充值	商品简单描述，该字段须严格按照规范传递，具体请见参数规定
			paraMap.put("out_trade_no",outtradeno);//	商户订单号	是	String(32)	2.01508E+13	商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号

			if(wxpayProperties.getIsSandbox()) amount = 1.01D;
			// 支付金额 **金额不能有小数点,单位是分!!**
			BigDecimal price = new BigDecimal(amount);
			BigDecimal beishu = new BigDecimal("100");
			BigDecimal priceFee = price.multiply(beishu);
			paraMap.put("total_fee",priceFee.toBigInteger().toString());//	总金额	是	Int	888	订单总金额，单位为分，详见支付金额

			paraMap.put("spbill_create_ip",ip);//	终端IP	是	String(64)	123.12.12.123	必须传正确的用户端IP,支持ipv4、ipv6格式，获取方式详见获取用户ip指引
			paraMap.put("notify_url",systemUrl+"/notify/wxpay");//	通知地址	是	String(256)	http://www.weixin.qq.com/wxpay/pay.php	接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
			paraMap.put("trade_type","MWEB");//	交易类型	是	String(16)	MWEB	H5支付的交易类型为MWEB
			paraMap.put("scene_info","{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \""+wapUrl+"\",\"wap_name\": \""+wapName+"\"}}");// 场景信息


			// MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
			String sign = PaymentKit.createSign(paraMap, wxpayProperties.getPaternerKey());
			paraMap.put("sign", sign);//	签名	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	签名，详见签名生成算法


			// 统一下单,向微信api发送数据
			logger.info("微信支付统一下单发送的数据: " + paraMap.toString());
			String xmlResult = WxPayApi.pushOrder(wxpayProperties.getIsSandbox(), paraMap);
			logger.info("微信支付统一下单接受返回的结果: " + xmlResult);
			// 转成xml
			Map<String, String> map = PaymentKit.xmlToMap(xmlResult);
			return jsonResultHelper.buildSuccessJsonResult(map);

		} catch (Exception e) {
			logger.error("微信支付统一下单异常,订单号：{} ",outtradeno, e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}

	/*public static void main(String[] args) {
		Map<String, String> params = new HashMap<>();
		params.put("mch_id","1531089441");
		params.put("nonce_str","1531089441");
		String paySign = PaymentKit.createSign(params, "441bf662209846edb9e633ec0b7c5c0f").toUpperCase();

		params.put("sign",paySign);

		System.out.println(WxPayApi.doPost("https://api.mch.weixin.qq.com/sandboxnew/pay/getsignkey",params));
	}*/
	
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
