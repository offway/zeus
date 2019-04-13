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
import cn.offway.zeus.domain.PhGoodsStock;
import cn.offway.zeus.domain.PhVoucherInfo;
import cn.offway.zeus.service.PhGoodsService;
import cn.offway.zeus.service.PhGoodsStockService;
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
	private PhGoodsStockService phGoodsStockService;
	
	
	@ApiOperation("优惠券列表")
	@GetMapping("/list")
	public JsonResult list(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("商品库存ID") @RequestParam(required = false) List<Long> goodsStockIds){
		
		List<PhVoucherInfo> list = new ArrayList<>();
		if(goodsStockIds.size()==0){
			list = phVoucherInfoService.findByUserIdOrderByCreateTimeDesc(userId);
		}else if(goodsStockIds.size()==1){
			PhGoodsStock phGoodsStock = phGoodsStockService.findOne(goodsStockIds.get(0));
			if(null==phGoodsStock){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);

			}
			list = phVoucherInfoService.list(userId, phGoodsStock.getGoodsId(), phGoodsStock.getBrandId(),phGoodsStock.getPrice());
		}else{
			List<Long> ids = new ArrayList<>();
			for (Long goodsStockId : goodsStockIds) {
				PhGoodsStock phGoodsStock = phGoodsStockService.findOne(goodsStockId);
				if(null==phGoodsStock){
					return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);

				}
				ids.addAll(phVoucherInfoService.ids(userId, phGoodsStock.getGoodsId(), phGoodsStock.getBrandId(),phGoodsStock.getPrice()));
			}
			
			list = phVoucherInfoService.findByIdInOrderByCreateTimeDesc(ids);
		}
		return jsonResultHelper.buildSuccessJsonResult(list);

	}
}
