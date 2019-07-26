package cn.offway.zeus.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.offway.zeus.domain.*;
import cn.offway.zeus.dto.*;
import cn.offway.zeus.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.offway.zeus.enums.ExpressCodeEnum;
import cn.offway.zeus.exception.StockException;
import cn.offway.zeus.repository.PhRefundRepository;
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
	
	@Autowired
	private PhRefundService phRefundService;

	@Autowired
	private VOrderRefundService vOrderRefundService;

	@Autowired
	private PhRefundGoodsService phRefundGoodsService;


	@ApiOperation("下订单")
	@PostMapping("/add")
	public JsonResult add(@RequestBody @ApiParam("请求参数") OrderAddDto orderAddDto){
		try {
			return phOrderInfoService.add(orderAddDto);
		}catch (StockException e) {
			e.printStackTrace();
			logger.error("减库存失败",e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.STOCK_SHORTAGE);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("下订单失败",e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
	@ApiOperation("取消订单")
	@PostMapping("/cancel")
	public JsonResult cancel(@ApiParam("预订单号") @RequestParam String preorderNo,
			@ApiParam("取消理由") @RequestParam(required = false) String remark){
		try {
			phPreorderInfoService.cancelOrder(preorderNo,remark);
			return jsonResultHelper.buildSuccessJsonResult(null);
		} catch (Exception e) {
			e.printStackTrace();
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
	@ApiOperation("退款不退钱")
	@PostMapping("/return")
	public JsonResult returnOrder(@ApiParam("预订单号") @RequestParam String preorderNo){
		try {
			phPreorderInfoService.returnOrder(preorderNo);
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
		
		Page<PhOrderInfo> page2 = phOrderInfoService.findByPage(userId, status.trim(), PageRequest.of(page,size));
		List<PhOrderInfo> phOrderInfos = page2.getContent();
		List<OrderInfoDto> dtos = getOrderInfoDtos(phOrderInfos);

		Page<OrderInfoDto> page3 = new PageImpl<>(dtos, PageRequest.of(page,size), page2.getTotalElements());
		return jsonResultHelper.buildSuccessJsonResult(page3);
	}

	private List<OrderInfoDto> getOrderInfoDtos(List<PhOrderInfo> phOrderInfos) {
		List<OrderInfoDto> dtos = new ArrayList<>();
		for (PhOrderInfo phOrderInfo : phOrderInfos) {
			OrderInfoDto dto = new OrderInfoDto();
			List<PhOrderGoods> goods = phOrderGoodsService.findByOrderNo(phOrderInfo.getOrderNo());
			if(null!=phOrderInfo){
				BeanUtils.copyProperties(phOrderInfo, dto);
			}
			dto.setGoods(goods);
			dtos.add(dto);
		}
		return dtos;
	}

	@ApiOperation("用户全部订单")
	@GetMapping("/user/all")
	public JsonResult userAll(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("页码,从0开始") @RequestParam int page,
			@ApiParam("页大小") @RequestParam int size){

		Page<VOrderRefund> page2 = vOrderRefundService.findByPage(userId, PageRequest.of(page,size));
		List<VOrderRefund> vOrderRefundList = page2.getContent();
		List<VOrderRefundDto> dtos = new ArrayList<>();
		for (VOrderRefund vOrderRefund : vOrderRefundList) {
			VOrderRefundDto dto = new VOrderRefundDto();
			if(null!=vOrderRefund){
				BeanUtils.copyProperties(vOrderRefund, dto);
			}

			List<Map<String,Object>> goods = new ArrayList<>();
			if ("order".equals(vOrderRefund.getStyle())){
				putGoods(vOrderRefund, goods);
				dto.setGoods(goods);
			}else{
				if("1".equals(vOrderRefund.getIsComplete())){
					putGoods(vOrderRefund, goods);
				}else{
					List<PhRefundGoods> phRefundGoodss = phRefundGoodsService.findByRefundId(vOrderRefund.getRefundId());
					for (PhRefundGoods phRefundGoods : phRefundGoodss) {
						PhOrderGoods phOrderGoods = phOrderGoodsService.findById(phRefundGoods.getOrderGoodsId());
						Map<String, Object> map1 = new HashMap<>();
						map1.put("image", phOrderGoods.getGoodsImage());
						map1.put("name", phOrderGoods.getGoodsName());
						map1.put("count", phRefundGoods.getGoodsCount());
						map1.put("price", MathUtils.mul(MathUtils.div(phOrderGoods.getPrice(), phOrderGoods.getGoodsCount(), 2), phRefundGoods.getGoodsCount()));
						map1.put("property", phOrderGoods.getRemark());
						goods.add(map1);
					}
				}
				dto.setGoods(goods);
			}

			dtos.add(dto);
		}

		Page<VOrderRefundDto> page3 = new PageImpl<>(dtos, PageRequest.of(page,size), page2.getTotalElements());
		return jsonResultHelper.buildSuccessJsonResult(page3);
	}

	private void putGoods(VOrderRefund vOrderRefund, List<Map<String, Object>> goods) {
		List<PhOrderGoods> phOrderGoodss = phOrderGoodsService.findByOrderNo(vOrderRefund.getOrderNo());
		for (PhOrderGoods phOrderGoods : phOrderGoodss) {
			Map<String, Object> map1 = new HashMap<>();
			map1.put("image", phOrderGoods.getGoodsImage());
			map1.put("name", phOrderGoods.getGoodsName());
			map1.put("count", phOrderGoods.getGoodsCount());
			map1.put("price", phOrderGoods.getPrice());
			map1.put("property", phOrderGoods.getRemark());
			goods.add(map1);
		}
	}

	@ApiOperation("用户待付款订单")
	@GetMapping("/user/pending")
	public JsonResult userPending(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("页码,从0开始") @RequestParam int page,
		    @ApiParam("页大小") @RequestParam int size){
		
		List<PreorderDto> preorderDtos = new ArrayList<>();
		Page<PhPreorderInfo> page2 = phPreorderInfoService.findByPage(userId, PageRequest.of(page,size));
		List<PhPreorderInfo> phOrderInfos = page2.getContent();
		for (PhPreorderInfo phPreorderInfo : phOrderInfos) {
			PreorderDto preorderDto = new PreorderDto();
			List<PhOrderInfo> orderInfos = phOrderInfoService.findByPreorderNoAndStatus(phPreorderInfo.getOrderNo(), "0");
			List<OrderInfoDto> dtos = getOrderInfoDtos(orderInfos);
			if(null!=phPreorderInfo){
				BeanUtils.copyProperties(phPreorderInfo, preorderDto);
			}
			preorderDto.setOrderInfos(dtos);
			preorderDtos.add(preorderDto);
			
		}
		
		Page<PreorderDto> page3 = new PageImpl<>(preorderDtos, PageRequest.of(page,size), page2.getTotalElements());
		return jsonResultHelper.buildSuccessJsonResult(page3);
	}
	
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
		Date createTime = null;
		Double sumPromotionAmount = 0D;
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
			createTime = phPreorderInfo.getCreateTime();
			sumPromotionAmount = phPreorderInfo.getPromotionAmount();
			List<PhOrderInfo> orderInfos = phOrderInfoService.findByPreorderNoAndStatus(phPreorderInfo.getOrderNo(), "0");
			for (PhOrderInfo phOrderInfo : orderInfos) {
				goods.addAll(phOrderGoodsService.findByOrderNo(phOrderInfo.getOrderNo()));
			}
			resultMap.put("goods", goods);
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
			createTime = phOrderInfo.getCreateTime();
			sumPromotionAmount = MathUtils.add(phOrderInfo.getPromotionAmount(),phOrderInfo.getPlatformPromotionAmount());
			goods = phOrderGoodsService.findByOrderNo(orderNo);

			//查询该笔订单是否有退款申请
			PhRefund phRefund = phRefundService.findByOrderNoEnd(orderNo);
			if (null != phRefund){
				String isComplete = phRefund.getIsComplete();
				List<OrderGoodsDto> orderGoodsDtos = new ArrayList<>();
				if("0".equals(isComplete)){
					//不是整单退款,查询退款商品
					List<PhRefundGoods> phRefundGoodsList = phRefundGoodsService.findByRefundId(phRefund.getId());
					for (PhOrderGoods phOrderGoods : goods) {
						OrderGoodsDto orderGoodsDto = new OrderGoodsDto();
						if(null!=phOrderGoods){
							BeanUtils.copyProperties(phOrderGoods,orderGoodsDto);
						}
						for (PhRefundGoods phRefundGoods : phRefundGoodsList) {
							if (phOrderGoods.getId().longValue() == phRefundGoods.getOrderGoodsId().longValue()){
								orderGoodsDto.setRefundStatus(phRefund.getStatus());
							}
						}
						orderGoodsDtos.add(orderGoodsDto);
					}
				}else{
					for (PhOrderGoods phOrderGoods : goods) {
						OrderGoodsDto orderGoodsDto = new OrderGoodsDto();
						if(null!=phOrderGoods){
							BeanUtils.copyProperties(phOrderGoods,orderGoodsDto);
						}
						orderGoodsDto.setRefundStatus(phRefund.getStatus());
						orderGoodsDtos.add(orderGoodsDto);
					}
				}
				resultMap.put("goods", orderGoodsDtos);
			}else{
				resultMap.put("goods", goods);
			}
			resultMap.put("canRefund", phRefundService.canRefund(orderNo));
		}
		

		resultMap.put("amount", amount);
		resultMap.put("orderNo", orderNo);
		resultMap.put("mailFee", mailFee);
		resultMap.put("price", price);
		resultMap.put("status", status);
		resultMap.put("voucherAmount", voucherAmount);
		resultMap.put("walletAmount", walletAmount);
		resultMap.put("receiptTime", receiptTime);
		resultMap.put("createTime", createTime);
		resultMap.put("sumPromotionAmount", sumPromotionAmount);

		
		//0-已下单,1-已付款,2-已发货,3-已收货,4-取消
		if("0".equals(status) || "1".equals(status)|| "4".equals(status)){
			PhAddress phAddress = phAddressService.findById(addrId);
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
			
			String mailNo = phOrderExpressInfo.getMailNo();
			if(StringUtils.isNotBlank(mailNo)){
				resultMap.put("express", queryExpress(phOrderExpressInfo));
			}
		}
		
		resultMap.put("address", addrMap);
		
		return jsonResultHelper.buildSuccessJsonResult(resultMap);

	}
	
	
	@ApiOperation("物流详情")
	@GetMapping("/express")
	public JsonResult express(@ApiParam("订单号") @RequestParam String orderNo){
		PhOrderExpressInfo phOrderExpressInfo = phOrderExpressInfoService.findByOrderNoAndType(orderNo, "0");
		return jsonResultHelper.buildSuccessJsonResult(queryExpress(phOrderExpressInfo));

	}

	private JSONObject queryExpress(PhOrderExpressInfo phOrderExpressInfo) {
//		String result = kuaidi100Service.query("yuantong", "805283162742333582", "18621866390");
		String result = kuaidi100Service.query(phOrderExpressInfo.getExpressCode(), phOrderExpressInfo.getMailNo(), phOrderExpressInfo.getToPhone());
		JSONObject jsonObject = JSON.parseObject(result);
		JSONArray list = new JSONArray();
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		if(null != jsonArray){
			list.addAll(jsonArray);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("context", "【正在发货】   物流单号："+ExpressCodeEnum.getByCode(phOrderExpressInfo.getExpressCode()).getDesc()+phOrderExpressInfo.getMailNo());
		String time = DateFormatUtils.format(phOrderExpressInfo.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
		map.put("time", time);
		map.put("ftime", time);
		list.add(map);
		jsonObject.put("data", list);
		return jsonObject;
	}
			
	
}
