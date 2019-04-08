package cn.offway.zeus.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.service.AlipayService;
import cn.offway.zeus.service.PhOrderInfoService;
import cn.offway.zeus.service.WxpayService;
import cn.offway.zeus.utils.IpUtil;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags={"支付"})
@RestController
@RequestMapping("/pay")
public class PayController {

	@Autowired
	private AlipayService alipayService;
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhOrderInfoService phOrderInfoService;
	
	@Autowired
	private WxpayService wxpayService;
	
	
	@ApiOperation("支付宝-生成 APP支付订单信息")
	@PostMapping("/alipay/trade")
	public JsonResult alipayTrade(){
		String outtradeno = phOrderInfoService.generateOrderNo("PH");
		return jsonResultHelper.buildSuccessJsonResult(alipayService.trade(outtradeno));
	}
	
	@ApiOperation("微信-统一下单")
	@PostMapping("/wx/trade")
	public JsonResult wxTrade(HttpServletRequest request){
		String outtradeno = phOrderInfoService.generateOrderNo("PH");
		return wxpayService.trade(outtradeno,IpUtil.getIpAddr(request));
	}
	
	
}
