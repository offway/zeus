package cn.offway.zeus.controller.activity;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BitField;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
	public JsonResult list(@ApiParam("用户unionid") @RequestParam(required = false) String unionid,
			@ApiParam("渠道,该字段为二进制位运算标识,0否1是,从右到左第一位表示H5,第二位表示小程序,第三位表示APP,第四位表示其他活动。如要查询APP则传参为0100,查询H5和小程序则传参0011以此类推") @RequestParam(required = false) String channel){
		try {
			channel = StringUtils.isBlank(channel)?"0100":channel;
			Map<String, List<ProductInfo>> resultMap = phProductInfoService.list(unionid,Integer.parseInt(channel,2));
			return jsonResultHelper.buildSuccessJsonResult(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("活动列表异常",e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
	@ApiOperation(value = "活动列表分页")
	@GetMapping("/listByPage")
	public JsonResult listByPage(
			@ApiParam("类型[0-进行中,1-未开始,2-已开奖]") @RequestParam String type,
			@ApiParam("用户unionid") @RequestParam(required = false) String unionid,
			@ApiParam("渠道,该字段为二进制位运算标识,0否1是,从右到左第一位表示H5,第二位表示小程序,第三位表示APP,第四位表示其他活动。如要查询APP则传参为0100,查询H5和小程序则传参0011以此类推") @RequestParam String channel,
			@ApiParam("页码,从0开始") @RequestParam int page,@ApiParam("页大小") @RequestParam int size){
		try {
			Page<ProductInfo> resultMap = phProductInfoService.listByPage(type,unionid,Integer.parseInt(channel,2),page,size);
			return jsonResultHelper.buildSuccessJsonResult(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("活动列表异常",e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
	
	@ApiOperation(value = "用户参与活动列表")
	@GetMapping("/user")
	public JsonResult user(
			@ApiParam("用户unionid") @RequestParam String unionid,
			@ApiParam("渠道,该字段为二进制位运算标识,0否1是,从右到左第一位表示H5,第二位表示小程序,第三位表示APP,第四位表示其他活动。如要查询APP则传参为0100,查询H5和小程序则传参0011以此类推") @RequestParam(required = false) String channel
			){
		try {
			channel = StringUtils.isBlank(channel)?"0":channel;
			return jsonResultHelper.buildSuccessJsonResult(phProductInfoService.findProductJoinByUnionid(unionid,Integer.parseInt(channel,2)));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户参与活动列表异常,unionid:{}",unionid,e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
	
}
