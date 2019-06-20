package cn.offway.zeus.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import cn.offway.zeus.domain.PhOrderExpressInfo;
import cn.offway.zeus.domain.PhOrderGoods;
import cn.offway.zeus.domain.PhOrderInfo;
import cn.offway.zeus.domain.PhRefund;
import cn.offway.zeus.domain.PhRefundGoods;
import cn.offway.zeus.dto.RefundDto;
import cn.offway.zeus.service.PhOrderGoodsService;
import cn.offway.zeus.service.PhRefundGoodsService;
import cn.offway.zeus.service.PhRefundService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.HttpClientUtil;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import cn.offway.zeus.utils.MathUtils;
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
	
	@Autowired
	private PhOrderGoodsService phOrderGoodsService;
	
	@Autowired
	private PhRefundGoodsService phRefundGoodsService;
	
	@Value("${is-prd}")
	private boolean isPrd;
	
	
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
//		if("0".equals(phRefund.getStatus())){
			phRefund.setStatus("5");
			phRefundService.save(phRefund);
			return jsonResultHelper.buildSuccessJsonResult(null);
//		}
//		return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);

	}
	
	@ApiOperation("填写物流单号")
	@PostMapping("/mailNo")
	public JsonResult mailNo(
			@ApiParam("退款申请ID") @RequestParam Long id,
			@ApiParam("物流单号") @RequestParam String mailNo,
			@ApiParam("快递公司编码") @RequestParam String expressCode){
		PhRefund phRefund = phRefundService.findOne(id);
		if("1".equals(phRefund.getStatus())){
			phRefund.setMailNo(mailNo);
			phRefund.setExpressCode(expressCode);
			phRefund.setStatus("2");
			phRefund = phRefundService.save(phRefund);
			
			if(isPrd){
				try {
					subscribeExpressInfo(phRefund);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("订阅快递物流推送异常,退款申请ID:{}",id,e);
				}
			}
			return jsonResultHelper.buildSuccessJsonResult(null);
		}
		return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);

	}
	
	private void subscribeExpressInfo(PhRefund phRefund) throws Exception {
	    String key = "uyUDaSuE5009";
	    Map<String, String> innerInnerParam = new HashMap<>();
	    innerInnerParam.put("callbackurl", "https://admin.offway.cn/callbackRefund/express?id=" +phRefund.getId() );
	    String innerInnerParamStr = JSON.toJSONString(innerInnerParam);
	    Map<String, String> innerParam = new HashMap<>();
	    innerParam.put("company", phRefund.getExpressCode());
	    innerParam.put("number", phRefund.getMailNo());
	    innerParam.put("key", key);
	    innerParam.put("parameters", innerInnerParamStr);
	    String innerParamStr = JSON.toJSONString(innerParam);
	    Map<String, String> param = new HashMap<>();
	    param.put("schema", "json");
	    param.put("param", innerParamStr);
	    String body = HttpClientUtil.post("https://poll.kuaidi100.com/poll", param);
	    logger.info("订阅快递物流推送响应参数:"+body);
	}
	
	@ApiOperation("退款详情")
	@GetMapping("/info")
	public JsonResult info(
			@ApiParam("退款申请ID") @RequestParam Long id){
		return phRefundService.info(id);
	}
	
	@ApiOperation("退换货列表")
	@GetMapping("/list")
	public JsonResult list(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("页码,从0开始") @RequestParam int page,
		    @ApiParam("页大小") @RequestParam int size){
		
		Page<PhRefund> pages = phRefundService.findByPage(userId, new PageRequest(page,size));
		List<Map<String, Object>> dtos = new ArrayList<>();
		List<PhRefund> phRefunds = pages.getContent();
		for (PhRefund phRefund : phRefunds) {
			Map<String, Object> map = new HashMap<>();
			map.put("orderNo", phRefund.getOrderNo());
			map.put("type", phRefund.getType());
			map.put("stauts", phRefund.getStatus());
			map.put("id", phRefund.getId());
			List<Map<String, Object>> goods = new ArrayList<>();

			if("1".equals(phRefund.getIsComplete())){
				List<PhOrderGoods> phOrderGoodss = phOrderGoodsService.findByOrderNo(phRefund.getOrderNo());
				for (PhOrderGoods phOrderGoods : phOrderGoodss) {
					Map<String, Object> map1 = new HashMap<>();
					map1.put("image", phOrderGoods.getGoodsImage());
					map1.put("name", phOrderGoods.getGoodsName());
					map1.put("count", phOrderGoods.getGoodsCount());
					map1.put("price", phOrderGoods.getPrice());
					map1.put("property", phOrderGoods.getRemark());
					goods.add(map1);
				}
			}else{
				List<PhRefundGoods> phRefundGoodss = phRefundGoodsService.findByRefundId(phRefund.getId());
				for (PhRefundGoods phRefundGoods : phRefundGoodss) {
					PhOrderGoods phOrderGoods = phOrderGoodsService.findOne(phRefundGoods.getOrderGoodsId());
					Map<String, Object> map1 = new HashMap<>();
					map1.put("image", phOrderGoods.getGoodsImage());
					map1.put("name", phOrderGoods.getGoodsName());
					map1.put("count", phRefundGoods.getGoodsCount());
					map1.put("price", MathUtils.mul(MathUtils.div(phOrderGoods.getPrice(), phOrderGoods.getGoodsCount(), 2), phRefundGoods.getGoodsCount()));
					map1.put("property", phOrderGoods.getRemark());
					goods.add(map1);
				}
			}
			map.put("goods", goods);
			dtos.add(map);
		}
		Page<Map<String, Object>> page3 = new PageImpl<>(dtos, new PageRequest(page,size), pages.getTotalElements());
		return jsonResultHelper.buildSuccessJsonResult(page3);

	}
}
