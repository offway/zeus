package cn.offway.zeus.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhRefund;
import cn.offway.zeus.dto.RefundDto;
import cn.offway.zeus.service.PhRefundService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags={"退款/退换货"})
@RestController
@RequestMapping("/refund")
public class RefundController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhRefundService phRefundService;
	
	
	@ApiOperation("退款申请初始化")
	@GetMapping("/init")
	public JsonResult init(String orderNo){
		return phRefundService.init(orderNo);

	}
	
	@ApiOperation("退款申请/修改")
	@PostMapping("/apply")
	public JsonResult apply(@ApiParam("请求参数") @RequestBody RefundDto refundDto){
		
		try {
			return phRefundService.apply(refundDto);
		} catch (Exception e) {
			e.printStackTrace();
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
 
		}
		
	}
	
	@ApiOperation("退款取消")
	@PostMapping("/cancel")
	public JsonResult refundCancel(@ApiParam("退款申请ID") @RequestParam Long id){
		PhRefund phRefund = phRefundService.findOne(id);
		if("0".equals(phRefund.getStatus())){
			phRefund.setStatus("5");
			phRefundService.save(phRefund);
			return jsonResultHelper.buildSuccessJsonResult(null);
		}
		return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);

	}
}
