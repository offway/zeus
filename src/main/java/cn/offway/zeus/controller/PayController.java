package cn.offway.zeus.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.api.response.AlipayTradeAppPayResponse;

import cn.offway.zeus.domain.PhPreorderInfo;
import cn.offway.zeus.service.AlipayService;
import cn.offway.zeus.service.PhPreorderInfoService;
import cn.offway.zeus.service.WxpayService;
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
		String body = "OFFWAY商品购买";
		String subject = "OFFWAY商品购买";
		double amount = phPreorderInfo.getAmount();
		AlipayTradeAppPayResponse response = alipayService.trade(preorderNo, body, subject, amount);
		if(response.isSuccess()){
			return jsonResultHelper.buildSuccessJsonResult(response.getBody());
		}else{
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
		
	}
	
	@ApiOperation("微信-统一下单")
	@PostMapping("/wx/trade")
	public JsonResult wxTrade(@ApiParam("预订单号") @RequestParam String preorderNo,HttpServletRequest request){
		PhPreorderInfo phPreorderInfo = phPreorderInfoService.findByOrderNoAndStatus(preorderNo, "0");
		if(null == phPreorderInfo){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
		}
		/*//修改订单状态位支付中
		phPreorderInfo.setStatus("3");
		phPreorderInfoService.save(phPreorderInfo);*/
		String body ="OFFWAY商品购买";
		double amount = phPreorderInfo.getAmount();
		return wxpayService.trade(preorderNo,IpUtil.getIpAddr(request),body,amount);
	}
	
	/*@ApiOperation("取消支付")
	@PostMapping("/cancel")
	public JsonResult cancel(@ApiParam("预订单号") @RequestParam String preorderNo){
		PhPreorderInfo phPreorderInfo = phPreorderInfoService.findByOrderNoAndStatus(preorderNo, "3");
		if(null == phPreorderInfo){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
		}
		//修改订单状态位支付中
		phPreorderInfo.setStatus("0");
		phPreorderInfoService.save(phPreorderInfo);

		return jsonResultHelper.buildSuccessJsonResult(null);
	}*/
	
	
}
