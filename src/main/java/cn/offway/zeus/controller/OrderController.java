package cn.offway.zeus.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhOrderGoods;
import cn.offway.zeus.domain.PhOrderInfo;
import cn.offway.zeus.domain.PhPreorderInfo;
import cn.offway.zeus.dto.OrderAddDto;
import cn.offway.zeus.dto.OrderInfoDto;
import cn.offway.zeus.dto.PreorderDto;
import cn.offway.zeus.service.PhOrderGoodsService;
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
	
	@Autowired
	private PhOrderGoodsService phOrderGoodsService;
	

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
	
	
	@ApiOperation("用户订单")
	@GetMapping("/user")
	public JsonResult user(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("状态[-1-全部,1-待发货,2-待收货]") @RequestParam String status,
			@ApiParam("页码,从0开始") @RequestParam int page,
		    @ApiParam("页大小") @RequestParam int size){
		
		Page<PhOrderInfo> page2 = phOrderInfoService.findByPage(userId, status.trim(), new PageRequest(page,size));
		List<PhOrderInfo> phOrderInfos = page2.getContent();
		List<OrderInfoDto> dtos = new ArrayList<>();
		for (PhOrderInfo phOrderInfo : phOrderInfos) {
			OrderInfoDto dto = new OrderInfoDto();
			List<PhOrderGoods> goods = phOrderGoodsService.findByOrderNo(phOrderInfo.getOrderNo());
			BeanUtils.copyProperties(phOrderInfo, dto);
			dto.setGoods(goods);
			dtos.add(dto);
		}
		
		Page<OrderInfoDto> page3 = new PageImpl<>(dtos, new PageRequest(page,size), page2.getTotalElements());
		return jsonResultHelper.buildSuccessJsonResult(page3);
	}
	
	@ApiOperation("用户待付款订单")
	@GetMapping("/user/pending")
	public JsonResult userPending(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("页码,从0开始") @RequestParam int page,
		    @ApiParam("页大小") @RequestParam int size){
		
		List<PreorderDto> preorderDtos = new ArrayList<>();
		Page<PhPreorderInfo> page2 = phPreorderInfoService.findByPage(userId, new PageRequest(page,size));
		List<PhPreorderInfo> phOrderInfos = page2.getContent();
		for (PhPreorderInfo phPreorderInfo : phOrderInfos) {
			PreorderDto preorderDto = new PreorderDto();
			List<PhOrderInfo> orderInfos = phOrderInfoService.findByPreorderNoAndStatus(phPreorderInfo.getOrderNo(), "0");
			List<OrderInfoDto> dtos = new ArrayList<>();
			for (PhOrderInfo phOrderInfo2 : orderInfos) {
				OrderInfoDto dto = new OrderInfoDto();
				List<PhOrderGoods> goods = phOrderGoodsService.findByOrderNo(phOrderInfo2.getOrderNo());
				BeanUtils.copyProperties(phOrderInfo2, dto);
				dto.setGoods(goods);
				dtos.add(dto);
			}
			BeanUtils.copyProperties(phPreorderInfo, preorderDto);
			preorderDto.setOrderInfos(dtos);
			preorderDtos.add(preorderDto);
			
		}
		
		Page<PreorderDto> page3 = new PageImpl<>(preorderDtos, new PageRequest(page,size), page2.getTotalElements());
		return jsonResultHelper.buildSuccessJsonResult(page3);
	}
	
	@ApiOperation("待付款订单详情")
	@GetMapping("/info/pending")
	public JsonResult infoPending(@ApiParam("预订单号") @RequestParam String preOrderNo){
		PhPreorderInfo phPreorderInfo = phPreorderInfoService.findByOrderNoAndStatus(preOrderNo, "2");
		PreorderDto preorderDto = new PreorderDto();
		List<PhOrderInfo> orderInfos = phOrderInfoService.findByPreorderNoAndStatus(phPreorderInfo.getOrderNo(), "4");
		List<OrderInfoDto> dtos = new ArrayList<>();
		for (PhOrderInfo phOrderInfo2 : orderInfos) {
			OrderInfoDto dto = new OrderInfoDto();
			List<PhOrderGoods> goods = phOrderGoodsService.findByOrderNo(phOrderInfo2.getOrderNo());
			BeanUtils.copyProperties(phOrderInfo2, dto);
			dto.setGoods(goods);
			dtos.add(dto);
		}
		BeanUtils.copyProperties(phPreorderInfo, preorderDto);
		preorderDto.setOrderInfos(dtos);
		
		return jsonResultHelper.buildSuccessJsonResult(preorderDto);

	}
	
	@ApiOperation("订单详情")
	@GetMapping("/info")
	public JsonResult info(@ApiParam("订单号") @RequestParam String orderNo){
		OrderInfoDto dto = new OrderInfoDto();
		PhOrderInfo phOrderInfo = phOrderInfoService.findByOrderNo(orderNo);
		List<PhOrderGoods> goods = phOrderGoodsService.findByOrderNo(orderNo);
		BeanUtils.copyProperties(phOrderInfo, dto);
		dto.setGoods(goods);
		return jsonResultHelper.buildSuccessJsonResult(dto);

	}
			
	
}
