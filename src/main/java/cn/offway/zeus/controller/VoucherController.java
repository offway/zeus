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

import cn.offway.zeus.dto.VoucherDto;
import cn.offway.zeus.service.PhVoucherInfoService;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags={"优惠券"})
@RestController
@RequestMapping("/voucher")
public class VoucherController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhVoucherInfoService phVoucherInfoService;
	
	@ApiOperation("可用优惠券列表")
	@PostMapping("/list")
	public JsonResult list(@ApiParam("请求参数") @RequestBody VoucherDto voucherDto){
		return jsonResultHelper.buildSuccessJsonResult(phVoucherInfoService.findAll(voucherDto));

	}
	
	@ApiOperation("用户优惠券列表")
	@GetMapping("/list")
	public JsonResult list(@ApiParam("用户ID") @RequestParam Long userId){
		return jsonResultHelper.buildSuccessJsonResult(phVoucherInfoService.findByUserIdOrderByCreateTimeDesc(userId));

	}
	
	@ApiOperation("优惠券详情")
	@GetMapping("/info")
	public JsonResult info(
			@ApiParam("优惠券ID") @RequestParam Long voucherId){
		return jsonResultHelper.buildSuccessJsonResult(phVoucherInfoService.findOne(voucherId));

	}
}
