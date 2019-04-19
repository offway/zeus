package cn.offway.zeus.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.alibaba.fastjson.JSON;

import cn.offway.zeus.domain.PhAddress;
import cn.offway.zeus.domain.PhOrderExpressInfo;
import cn.offway.zeus.domain.PhOrderGoods;
import cn.offway.zeus.domain.PhOrderInfo;
import cn.offway.zeus.domain.PhPreorderInfo;
import cn.offway.zeus.dto.OrderAddDto;
import cn.offway.zeus.dto.OrderInfoDto;
import cn.offway.zeus.dto.PreorderDto;
import cn.offway.zeus.exception.StockException;
import cn.offway.zeus.service.Kuaidi100Service;
import cn.offway.zeus.service.PhAddressService;
import cn.offway.zeus.service.PhOrderExpressInfoService;
import cn.offway.zeus.service.PhOrderGoodsService;
import cn.offway.zeus.service.PhOrderInfoService;
import cn.offway.zeus.service.PhPreorderInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import cn.offway.zeus.utils.MathUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Api(tags={"订单"})
@RestController
@RequestMapping("/order")
public class OrderController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhOrderInfoService phOrderInfoService;
	
	@Autowired
	private PhPreorderInfoService phPreorderInfoService;
	
	@Autowired
	private PhOrderGoodsService phOrderGoodsService;
	
	@Autowired
	private PhAddressService phAddressService;
	
	@Autowired
	private Kuaidi100Service kuaidi100Service;
	
	@Autowired
	private PhOrderExpressInfoService phOrderExpressInfoService;
	
	

	@ApiOperation("下订单")
	@PostMapping("/add")
	public JsonResult add(@RequestBody @ApiParam("请求参数") OrderAddDto orderAddDto){
		try {
			return phOrderInfoService.add(orderAddDto);
		}catch (StockException e) {
			e.printStackTrace();
			logger.info("减库存失败",e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.STOCK_SHORTAGE);
		}catch (Exception e) {
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
	
	@ApiOperation("确认收货")
	@PostMapping("/receipt")
	public JsonResult receipt(@ApiParam("订单号") @RequestParam String orderNo){
		try {
			PhOrderInfo phOrderInfo = phOrderInfoService.findByOrderNo(orderNo);
			if("2".equals(phOrderInfo.getStatus())){
				//已收货
				phOrderInfo.setStatus("3");
				phOrderInfo.setReceiptTime(new Date());
				phOrderInfoService.save(phOrderInfo);
			}
			return jsonResultHelper.buildSuccessJsonResult(null);
		} catch (Exception e) {
			e.printStackTrace();
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
	@ApiOperation("删除订单")
	@PostMapping("/del")
	public JsonResult del(@ApiParam("订单号") @RequestParam String orderNo){
		try {
			PhOrderInfo phOrderInfo = phOrderInfoService.findByOrderNo(orderNo);
			phOrderInfo.setIsHidden("1");
			phOrderInfoService.save(phOrderInfo);
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
	
	/*@SuppressWarnings("unchecked")
	@ApiOperation("待付款订单详情")
	@GetMapping("/info/pending")
	public JsonResult infoPending(@ApiParam("预订单号") @RequestParam String preOrderNo){
		
		PhPreorderInfo phPreorderInfo = phPreorderInfoService.findByOrderNoAndStatus(preOrderNo, "0");
		Map<String, Object> resultMap = new HashMap<>();//JSON.parseObject(JSON.toJSONString(phPreorderInfo), Map.class);
		
		resultMap.put("amount", phPreorderInfo.getAmount());
		resultMap.put("orderNo", phPreorderInfo.getOrderNo());
		resultMap.put("mailFee", phPreorderInfo.getMailFee());
		resultMap.put("price", phPreorderInfo.getPrice());
		resultMap.put("status", phPreorderInfo.getStatus());
		resultMap.put("voucherAmount", phPreorderInfo.getVoucherAmount());
		resultMap.put("walletAmount", phPreorderInfo.getWalletAmount());
		
		
		PhAddress phAddress = phAddressService.findOne(phPreorderInfo.getAddrId());
		Map<String, Object> addrMap = new HashMap<>();
		addrMap.put("city", phAddress.getCity());
		addrMap.put("content", phAddress.getContent());
		addrMap.put("county", phAddress.getCounty());
		addrMap.put("phone", phAddress.getPhone());
		addrMap.put("province", phAddress.getProvince());
		addrMap.put("realName", phAddress.getRealName());
		resultMap.put("address", addrMap);
		
		
		List<PhOrderGoods> goods = new ArrayList<>();
		
		List<PhOrderInfo> orderInfos = phOrderInfoService.findByPreorderNoAndStatus(phPreorderInfo.getOrderNo(), "0");
		for (PhOrderInfo phOrderInfo : orderInfos) {
			goods.addAll(phOrderGoodsService.findByOrderNo(phOrderInfo.getOrderNo()));
		}
		
		resultMap.put("goods", goods);
		
		return jsonResultHelper.buildSuccessJsonResult(resultMap);

	}*/
	
	@ApiOperation("订单详情")
	@GetMapping("/info")
	public JsonResult info(
			@ApiParam("预订单号[仅支持待付款状态下查询]") @RequestParam(required = false) String preOrderNo,
			@ApiParam("订单号") @RequestParam(required = false) String orderNo){

		
		Double amount = 0D;
		Double mailFee = 0D;
		Double price = 0D;
		String status = null;
		Double voucherAmount = 0D;
		Double walletAmount = 0D;
		Long addrId = null;
		Date receiptTime = null;
		List<PhOrderGoods> goods = new ArrayList<>();
		Map<String, Object> addrMap = new HashMap<>();


		
		Map<String, Object> resultMap = new HashMap<>();

		if(StringUtils.isNotBlank(preOrderNo)){
			PhPreorderInfo phPreorderInfo = phPreorderInfoService.findByOrderNoAndStatus(preOrderNo, "0");
			amount = phPreorderInfo.getAmount();
			orderNo = phPreorderInfo.getOrderNo();
			mailFee = phPreorderInfo.getMailFee();
			price = phPreorderInfo.getPrice();
			status = "0";
			voucherAmount = phPreorderInfo.getVoucherAmount();
			walletAmount = phPreorderInfo.getWalletAmount();
			addrId = phPreorderInfo.getAddrId();
			List<PhOrderInfo> orderInfos = phOrderInfoService.findByPreorderNoAndStatus(phPreorderInfo.getOrderNo(), "0");
			for (PhOrderInfo phOrderInfo : orderInfos) {
				goods.addAll(phOrderGoodsService.findByOrderNo(phOrderInfo.getOrderNo()));
			}
			
		}else{
			PhOrderInfo phOrderInfo = phOrderInfoService.findByOrderNo(orderNo);
			amount = phOrderInfo.getAmount();
			orderNo = phOrderInfo.getOrderNo();
			mailFee = phOrderInfo.getMailFee();
			price = phOrderInfo.getPrice();
			status = phOrderInfo.getStatus();
			receiptTime = phOrderInfo.getReceiptTime();
			voucherAmount = MathUtils.add(phOrderInfo.getMVoucherAmount(), phOrderInfo.getPVoucherAmount());
			walletAmount = phOrderInfo.getWalletAmount();
			addrId = phOrderInfo.getAddrId();
			goods = phOrderGoodsService.findByOrderNo(orderNo);

			
		}
		
		resultMap.put("goods", goods);
		resultMap.put("amount", amount);
		resultMap.put("orderNo", orderNo);
		resultMap.put("mailFee", mailFee);
		resultMap.put("price", price);
		resultMap.put("status", status);
		resultMap.put("voucherAmount", voucherAmount);
		resultMap.put("walletAmount", walletAmount);
		resultMap.put("receiptTime", receiptTime);
		
		
		//0-已下单,1-已付款,2-已发货,3-已收货,4-取消
		if("0".equals(status) || "1".equals(status)|| "4".equals(status)){
			PhAddress phAddress = phAddressService.findOne(addrId);
			addrMap.put("city", phAddress.getCity());
			addrMap.put("content", phAddress.getContent());
			addrMap.put("county", phAddress.getCounty());
			addrMap.put("phone", phAddress.getPhone());
			addrMap.put("province", phAddress.getProvince());
			addrMap.put("realName", phAddress.getRealName());
		}else{
			
			PhOrderExpressInfo phOrderExpressInfo = phOrderExpressInfoService.findByOrderNoAndType(orderNo, "0");
			
			addrMap.put("city", phOrderExpressInfo.getToCity());
			addrMap.put("content", phOrderExpressInfo.getToContent());
			addrMap.put("county", phOrderExpressInfo.getToCounty());
			addrMap.put("phone", phOrderExpressInfo.getToPhone());
			addrMap.put("province", phOrderExpressInfo.getToProvince());
			addrMap.put("realName", phOrderExpressInfo.getToRealName());
			
			if(StringUtils.isNotBlank(phOrderExpressInfo.getMailNo())){
				//kuaidi100Service.query(phOrderInfo.getExpressCode(), phOrderInfo.getMailNo(), phAddress.getPhone())
				//String result = "{\"message\":\"ok\",\"nu\":\"805283162742333582\",\"ischeck\":\"0\",\"condition\":\"00\",\"com\":\"yuantong\",\"status\":\"200\",\"state\":\"0\",\"data\":[{\"time\":\"2019-04-10 02:38:48\",\"ftime\":\"2019-04-10 02:38:48\",\"context\":\"【江门转运中心】 已发出 下一站 【上海转运中心】\"},{\"time\":\"2019-04-10 02:37:46\",\"ftime\":\"2019-04-10 02:37:46\",\"context\":\"【江门转运中心】 已收入\"},{\"time\":\"2019-04-10 02:22:42\",\"ftime\":\"2019-04-10 02:22:42\",\"context\":\"【广东省中山市板芙镇公司】 已收件\"},{\"time\":\"2019-04-09 19:34:30\",\"ftime\":\"2019-04-09 19:34:30\",\"context\":\"【广东省中山市板芙镇公司】 取件人: 朱华文 已收件\"}]}";
				String result = kuaidi100Service.query("yuantong", "805283162742333582", "18621866390");
				resultMap.put("express", JSON.parse(result));
			}
		}
		
		resultMap.put("address", addrMap);

		
		
		return jsonResultHelper.buildSuccessJsonResult(resultMap);

	}
			
	
}
