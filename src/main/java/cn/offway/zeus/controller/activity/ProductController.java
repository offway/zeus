package cn.offway.zeus.controller.activity;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.dto.ProductInfo;
import cn.offway.zeus.service.PhProductInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 活动产品
 * @author wn
 *
 */
@RestController
@RequestMapping("/product")
public class ProductController {
	
	@Autowired
	private PhProductInfoService phProductInfoService;
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@ApiOperation(value = "活动列表")
	@GetMapping("/list")
	public JsonResult list(@ApiParam("用户unionid") @RequestParam(required = false) String unionid){
		try {
			Map<String, List<ProductInfo>> resultMap = phProductInfoService.list(unionid);
			return jsonResultHelper.buildSuccessJsonResult(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("活动列表异常",e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
	@ApiOperation(value = "用户参与活动列表")
	@GetMapping("/user")
	public JsonResult user(@ApiParam("用户unionid") @RequestParam String unionid){
		try {
			return jsonResultHelper.buildSuccessJsonResult(phProductInfoService.findProductJoinByUnionid(unionid));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户参与活动列表异常,unionid:{}",unionid,e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
}
