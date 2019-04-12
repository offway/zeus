package cn.offway.zeus.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhGoods;
import cn.offway.zeus.domain.PhVoucherInfo;
import cn.offway.zeus.service.PhGoodsService;
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
	private PhGoodsService phGoodsService;
	
	
	@ApiOperation("优惠券列表")
	@GetMapping("/list")
	public JsonResult list(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("商品ID") @RequestParam(required = false) Long goodsId){
		
		List<PhVoucherInfo> list = new ArrayList<>();
		if(null == goodsId){
			list = phVoucherInfoService.findByUserIdOrderByCreateTimeDesc(userId);
		}else{
			PhGoods phGoods = phGoodsService.findOne(goodsId);
			if(null==phGoods){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);

			}
			Long brandId = phGoods.getBrandId();
			list = phVoucherInfoService.list(userId, goodsId, brandId);
		}
		return jsonResultHelper.buildSuccessJsonResult(list);

	}
}
