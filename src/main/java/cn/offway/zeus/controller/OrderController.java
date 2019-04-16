package cn.offway.zeus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.dto.OrderAddDto;
import cn.offway.zeus.service.PhOrderInfoService;
import cn.offway.zeus.service.PhPreorderInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Api(tags={"订单"})
@RestController
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhOrderInfoService phOrderInfoService;
	
	@Autowired
	private PhPreorderInfoService phPreorderInfoService;

	@ApiOperation("下订单")
	@PostMapping("/add")
	public JsonResult add(@RequestBody @ApiParam("请求参数") OrderAddDto orderAddDto){
		try {
			return phOrderInfoService.add(orderAddDto);
		} catch (Exception e) {
			e.printStackTrace();
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
	@ApiOperation("取消订单")
	@PostMapping("/cancel")
	public JsonResult cancel(@ApiParam("预订单号") @RequestParam String preorderNo){
		try {
			phPreorderInfoService.cancelOrder(preorderNo);
			return jsonResultHelper.buildSuccessJsonResult(null);
		} catch (Exception e) {
			e.printStackTrace();
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
}
