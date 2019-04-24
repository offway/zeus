package cn.offway.zeus.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alipay.api.internal.util.AlipaySignature;
import com.jpay.ext.kit.PaymentKit;

import cn.offway.zeus.domain.PhPreorderInfo;
import cn.offway.zeus.dto.AlipayNotify;
import cn.offway.zeus.properties.WxpayProperties;
import cn.offway.zeus.service.AlipayService;
import cn.offway.zeus.service.PhPreorderInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;



/**
 * 系统通知
 * @author wn
 *
 */
@Api(tags={"系统通知对外服务"})
@RestController
@RequestMapping("/notify")
public class NotifyController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private AlipayService alipayService;
	
	@Autowired
	private WxpayProperties wxpayProperties;
	
	@Autowired
	private PhPreorderInfoService phPreorderInfoService;
	

	/**
	 * 支付宝通知
	 * @param request
	 * @return
	 */
	@ApiOperation("支付宝支付结果通知")
	@PostMapping(value="/alipay",produces="text/html;charset=UTF-8")
	public String alipay(HttpServletRequest request,AlipayNotify alipayNotify){
		try {
			logger.info("支付宝通知:"+JSON.toJSONString(alipayNotify));
			boolean validate = alipayService.validate(request);
			logger.info("支付宝通知验证结果:"+validate);
			if(validate){
				String trade_status = alipayNotify.getTrade_status();
				/**
				 * 	WAIT_BUYER_PAY	交易创建，等待买家付款
				TRADE_CLOSED	未付款交易超时关闭，或支付完成后全额退款
				TRADE_SUCCESS	交易支付成功
				TRADE_FINISHED	交易结束，不可退款
				 */
				
				String out_trade_no = alipayNotify.getOut_trade_no();
				
				phPreorderInfoService.alipay(trade_status, out_trade_no);
				return "success";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("支付宝通知异常",e);
		}
		return "fail";
		
	}
	
	@ApiOperation("微信支付结果通知")
	@PostMapping("/wxpay")
    public String notify(@RequestBody String xmlMsg){
        // 支付结果通用通知文档: https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7
        try {
			System.out.println("支付通知="+xmlMsg);
			Map<String, String> params = PaymentKit.xmlToMap(xmlMsg);
			String result_code  = params.get("result_code");


			//校验返回来的支付结果,根据已经配置的密钥
			if(PaymentKit.verifyNotify(params, wxpayProperties.getPaternerKey())){
				//校验通过. 更改订单状态为已支付, 修改库存
				String mch_id = params.get("mch_id"); //商户号
				String openid = params.get("openid");  //用户标识
				String out_trade_no = params.get("out_trade_no"); //商户订单号
				
				phPreorderInfoService.wxpay(result_code, out_trade_no);
				
				 return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("微信支付结果通知异常，请求参数：{}",e,xmlMsg);

		}
		 return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERROR]]></return_msg></xml>";

    }
	
	/*@ApiOperation("支付成功")
	@PostMapping("/ok")
    public String ok(@ApiParam("预订单号") @RequestParam String preorderNo) throws Exception{
		phPreorderInfoService.wxpay("SUCCESS", preorderNo);
	    return "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
	}*/
	
}
