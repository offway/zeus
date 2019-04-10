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

import cn.offway.zeus.domain.PhBrand;
import cn.offway.zeus.dto.BrandDto;
import cn.offway.zeus.service.PhBrandService;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags={"品牌"})
@RestController
@RequestMapping("/brand")
public class BrandController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhBrandService phBrandService;

	@ApiOperation("品牌列表")
	@GetMapping("/list")
	public JsonResult list(){
		return jsonResultHelper.buildSuccessJsonResult(phBrandService.findAll());
	}
	
	@ApiOperation("品牌列表分页")
	@PostMapping("/listByPage")
	public JsonResult listByPage(@RequestBody @ApiParam("品牌属性") BrandDto brandDto) throws Exception{
	    
		Page<PhBrand> pages = phBrandService.findByPage(brandDto, new PageRequest(brandDto.getPage(), brandDto.getSize()));

		return jsonResultHelper.buildSuccessJsonResult(pages);
	}
	
	@ApiOperation("品牌详情")
	@GetMapping("/info")
	public JsonResult info(@ApiParam("品牌ID")@RequestParam Long id){
		return jsonResultHelper.buildSuccessJsonResult(phBrandService.findOne(id));
	}
	
	
}
