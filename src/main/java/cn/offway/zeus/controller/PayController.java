package cn.offway.zeus.controller;

import javax.servlet.http.HttpServletRequest;

import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.domain.PhWxuserInfo;
import cn.offway.zeus.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alipay.api.response.AlipayTradeAppPayResponse;

import cn.offway.zeus.domain.PhPreorderInfo;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.IpUtil;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags={"支付"})
@RestController
@RequestMapping("/pay")
public class PayController {

	@Autowired
	private AlipayService alipayService;
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private WxpayService wxpayService;
	
	@Autowired
	private PhPreorderInfoService phPreorderInfoService;

	@Autowired
	private PhUserInfoService phUserInfoService;

	@Autowired
	private PhWxuserInfoService phWxuserInfoService;

	
	
	@ApiOperation("支付宝-生成 APP支付订单信息")
	@PostMapping("/alipay/trade")
	public JsonResult alipayTrade(@ApiParam("预订单号") @RequestParam String preorderNo){
		
		PhPreorderInfo phPreorderInfo = phPreorderInfoService.findByOrderNoAndStatus(preorderNo, "0");
		if(null == phPreorderInfo){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
		}
		/*//修改订单状态位支付中
		phPreorderInfo.setStatus("3");
		phPreorderInfoService.save(phPreorderInfo);*/
		String body = "很潮商品购买";
		String subject = "很潮商品购买";
		double amount = phPreorderInfo.getAmount();
		AlipayTradeAppPayResponse response = alipayService.trade(preorderNo, body, subject, amount);
		if(response.isSuccess()){
			return jsonResultHelper.buildSuccessJsonResult(response.getBody());
		}else{
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
		
	}

	@ApiOperation("支付宝手机网站支付")
	@GetMapping(value = "/alipay/trade/web.html",produces="text/html;charset=UTF-8")
	public String tradeByWeb(
			@ApiParam("预订单号") @RequestParam String preorderNo,
			@ApiParam("支付宝服务器主动通知商户服务器里指定的页面http/https路径。") @RequestParam String returnUrl){
		PhPreorderInfo phPreorderInfo = phPreorderInfoService.findByOrderNoAndStatus(preorderNo, "0");
		if(null == phPreorderInfo){
			return "PARAM_ERROR";
		}
		String subject = "很潮商品购买";
		double amount = phPreorderInfo.getAmount();
		return alipayService.tradeByWeb(preorderNo,amount,subject,returnUrl);
	}
	
	@ApiOperation("微信-统一下单")
	@PostMapping("/wx/trade")
	public JsonResult wxTrade(
			@ApiParam("预订单号") @RequestParam String preorderNo,
			@ApiParam("交易类型[APP-APP支付,JSAPI-小程序,MWEB-H5支付") @RequestParam(defaultValue = "APP") String tradeType,
			@ApiParam("openid[支付方式为小程序时该参数必传]") @RequestParam(required = false) String openid,
			@ApiParam("WAP网站URL地址[支付方式为MWEB时该参数必传]") @RequestParam(required = false) String wapUrl,
			@ApiParam("WAP网站名[支付方式为MWEB时该参数必传]") @RequestParam(required = false) String wapName,
			HttpServletRequest request){

		PhPreorderInfo phPreorderInfo = phPreorderInfoService.findByOrderNoAndStatus(preorderNo, "0");
		if(null == phPreorderInfo){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
		}

		String body ="OFFWAY商品购买";
		double amount = phPreorderInfo.getAmount();

		if("JSAPI".equals(tradeType)){
			if(StringUtils.isBlank(openid)){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_MISS);
			}
			return wxpayService.trade_JSAPI(preorderNo,IpUtil.getIpAddr(request),body,amount,openid);
		}else if("MWEB".equals(tradeType)){
			if(StringUtils.isBlank(wapUrl) || StringUtils.isBlank(wapName)){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_MISS);
			}
			return wxpayService.trade_MWEB(preorderNo,IpUtil.getIpAddr(request),body,amount,wapUrl,wapName);
		}else{
			return wxpayService.trade_APP(preorderNo,IpUtil.getIpAddr(request),body,amount);

		}

	}

}
