package cn.offway.zeus.controller;

import java.util.Arrays;

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
import cn.offway.zeus.service.PhConfigService;
import cn.offway.zeus.service.PhVoucherInfoService;
import cn.offway.zeus.utils.CommonResultCode;
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
	
	@Autowired
	private PhConfigService phConfigService;
	
	
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
	
	@ApiOperation("优惠券领取")
	@PostMapping("/give")
	public JsonResult give(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("配置") @RequestParam String config){
		String content = phConfigService.findContentByName(config);
		Long voucherProjectId = Long.parseLong(content);
		return giveVoucher(userId, voucherProjectId);

	}

	@ApiOperation("根据优惠券方案ID领取优惠券")
	@PostMapping("/giveByVpId")
	public JsonResult give(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("优惠券方案ID") @RequestParam Long voucherProjectId){
		
		JsonResult jsonResult = giveVoucher(userId, voucherProjectId);
		jsonResult.setData(phVoucherInfoService.findId(userId, voucherProjectId));
		return jsonResult;

	}
	
	@ApiOperation("领取并使用优惠券")
	@PostMapping("/use")
	public JsonResult use(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("优惠券方案ID") @RequestParam Long voucherProjectId){
		
		Long id = phVoucherInfoService.findId(userId, voucherProjectId);
		if(null == id){
			boolean result = phVoucherInfoService.giveVoucher(userId, voucherProjectId);
			if(result){
				id = phVoucherInfoService.findId(userId, voucherProjectId);
				return jsonResultHelper.buildSuccessJsonResult(id);
			}else{
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
			}
		}
		return jsonResultHelper.buildSuccessJsonResult(id);
	}
	
	
	private JsonResult giveVoucher(Long userId, Long voucherProjectId) {
		int count = phVoucherInfoService.countByUserIdAndVoucherProjectIdAndStatus(userId,voucherProjectId,"0");
		if(count>0){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.VOUCHER_GIVED);
		}
		boolean result = phVoucherInfoService.giveVoucher(userId, voucherProjectId);
		if(result){
			return jsonResultHelper.buildSuccessJsonResult(null);
		}else{
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
}
