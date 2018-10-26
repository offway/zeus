package cn.offway.zeus.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhWxuserInfo;
import cn.offway.zeus.dto.WxuserInfo;
import cn.offway.zeus.service.PhWxuserInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 用户相关
 * @author wn
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhWxuserInfoService phWxuserInfoService;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@ApiOperation("微信用户信息保存")
	@PostMapping("/wx")
	public JsonResult wx(@ApiParam("微信用户信息") @RequestBody WxuserInfo wxuserInfo){
		try {
			
			PhWxuserInfo phWxuserInfo = phWxuserInfoService.findByUnionid(wxuserInfo.getUnionid());
			if(null == phWxuserInfo){
				phWxuserInfo = new PhWxuserInfo();
			}
			BeanUtils.copyProperties(wxuserInfo, phWxuserInfo);
			phWxuserInfo.setCreateTime(new Date());
			phWxuserInfo.setAppDownload("1");
			phWxuserInfoService.save(phWxuserInfo);
			return jsonResultHelper.buildSuccessJsonResult(null);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("微信用户信息保存",wxuserInfo.toString(),e);
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
		}
	}
}
