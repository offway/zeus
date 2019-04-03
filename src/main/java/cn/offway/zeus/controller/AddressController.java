package cn.offway.zeus.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhAddress;
import cn.offway.zeus.service.PhAddressService;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags={"地址"})
@RestController
@RequestMapping("/addr")
public class AddressController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhAddressService phAddressService;
	
	
	@ApiOperation("保存地址")
	@PostMapping("/save")
	public JsonResult save(@RequestBody @ApiParam("地址信息") PhAddress phAddress){
		phAddress.setCreateTime(new Date());
		return jsonResultHelper.buildSuccessJsonResult(phAddressService.save(phAddress));
	}
	
	@ApiOperation("查询地址列表")
	@GetMapping("/list")
	public JsonResult list(@RequestParam Long userId){
		return jsonResultHelper.buildSuccessJsonResult(phAddressService.findByUserId(userId));
	}
	
	@ApiOperation("查询地址")
	@GetMapping("/info")
	public JsonResult info(@ApiParam("地址ID") @RequestParam Long id){
		return jsonResultHelper.buildSuccessJsonResult(phAddressService.findOne(id));
	}
	
	@ApiOperation("删除地址")
	@PostMapping("/del")
	public JsonResult del(@ApiParam("地址ID") @RequestParam Long id){
		phAddressService.delete(id);
		return jsonResultHelper.buildSuccessJsonResult(null);
	}
	
	@ApiOperation("查询默认地址")
	@GetMapping("/default")
	public JsonResult defaultAdrr(@RequestParam Long userId){
		return jsonResultHelper.buildSuccessJsonResult(phAddressService.findDefault(userId));
	}
	
}
