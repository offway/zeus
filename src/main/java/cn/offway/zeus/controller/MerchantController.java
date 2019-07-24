package cn.offway.zeus.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhBrand;
import cn.offway.zeus.domain.PhMerchant;
import cn.offway.zeus.dto.MerchantDto;
import cn.offway.zeus.dto.MerchantInfoDto;
import cn.offway.zeus.service.PhBrandService;
import cn.offway.zeus.service.PhMerchantService;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags={"商户"})
@RestController
@RequestMapping("/merchant")
public class MerchantController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhMerchantService phMerchantService;
	
	@Autowired
	private PhBrandService phBrandService;
	
	@ApiOperation("商户列表")
	@PostMapping("/listByPage")
	public JsonResult listByPage(
			@ApiParam("商户属性") @RequestBody MerchantDto merchantDto) throws Exception{
	    
		Page<PhMerchant> pages = phMerchantService.findByPage(merchantDto, PageRequest.of(merchantDto.getPage(), merchantDto.getSize()));

		return jsonResultHelper.buildSuccessJsonResult(pages);
	}
	
	@ApiOperation("商户品牌列表")
	@GetMapping("/brands")
	public JsonResult listByPage(
			@ApiParam("商户Id") @RequestParam Long id) throws Exception{
		return jsonResultHelper.buildSuccessJsonResult(phBrandService.findByMerchantId(id));
	}
	
	@ApiOperation("商户详情")
	@GetMapping("/info")
	public JsonResult info(
			@ApiParam("商户Id") @RequestParam Long id) throws Exception{
		PhMerchant phMerchant = phMerchantService.getOne(id);
		MerchantInfoDto dto = new MerchantInfoDto();
		BeanUtils.copyProperties(phMerchant, dto);
		List<PhBrand> brands = phBrandService.findByMerchantId(id);
		dto.setBrands(brands);
		return jsonResultHelper.buildSuccessJsonResult(dto);
	}
}
