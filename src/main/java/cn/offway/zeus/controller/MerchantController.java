package cn.offway.zeus.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhMerchant;
import cn.offway.zeus.dto.BrandDto;
import cn.offway.zeus.dto.MerchantDto;
import cn.offway.zeus.repository.PhMerchantBrandRepository;
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
	private PhMerchantBrandRepository phMerchantBrandRepository;
	
	@ApiOperation("商户列表")
	@PostMapping("/listByPage")
	public JsonResult listByPage(
			@ApiParam("商户属性") @RequestBody MerchantDto merchantDto) throws Exception{
	    
		Page<PhMerchant> pages = phMerchantService.findByPage(merchantDto, new PageRequest(merchantDto.getPage(), merchantDto.getSize()));

		return jsonResultHelper.buildSuccessJsonResult(pages);
	}
	
	@ApiOperation("商户品牌列表")
	@GetMapping("/brands")
	public JsonResult listByPage(
			@ApiParam("商户Id") @RequestParam Long merchantId) throws Exception{
		return jsonResultHelper.buildSuccessJsonResult(phMerchantBrandRepository.findByMerchantId(merchantId));
	}
}
