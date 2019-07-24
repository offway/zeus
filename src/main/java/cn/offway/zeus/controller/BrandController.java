package cn.offway.zeus.controller;

import java.util.ArrayList;
import java.util.List;

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

import cn.offway.zeus.domain.PhBrand;
import cn.offway.zeus.domain.PhGoods;
import cn.offway.zeus.dto.BrandDto;
import cn.offway.zeus.dto.BrandRecommendDto;
import cn.offway.zeus.service.PhBrandService;
import cn.offway.zeus.service.PhGoodsService;
import cn.offway.zeus.utils.CommonResultCode;
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
	
	@Autowired
	private PhGoodsService phGoodsService;

	@ApiOperation("全部品牌")
	@GetMapping("/list")
	public JsonResult list(
			@ApiParam("类型[0-国内品牌，1-国际品牌]") @RequestParam(required = false) String type){
		return jsonResultHelper.buildSuccessJsonResult(phBrandService.findAll(type));
	}
	
	@ApiOperation("推荐品牌")
	@PostMapping("/recommends")
	public JsonResult recommends(@RequestBody @ApiParam("品牌属性") BrandDto brandDto) throws Exception{
	    
		Page<PhBrand> pages = phBrandService.findByPage(brandDto, "1", PageRequest.of(brandDto.getPage(), brandDto.getSize()));
		
		List<PhBrand> phBrands = pages.getContent();
		List<BrandRecommendDto> dtos = new ArrayList<>();
	
		for (PhBrand phBrand : phBrands) {
			BrandRecommendDto dto = new BrandRecommendDto();
			BeanUtils.copyProperties(phBrand, dto);
			List<PhGoods> goods = new ArrayList<>();
			if("0".equals(brandDto.getType())){
				goods = phGoodsService.findBrandRecommend(phBrand.getId());
			}
			dto.setGoods(goods);
			dtos.add(dto);
		}
		return jsonResultHelper.buildSuccessJsonResult(new PageImpl<>(dtos, PageRequest.of(brandDto.getPage(), brandDto.getSize()), pages.getTotalElements()));
	}
	
	@ApiOperation("品牌列表分页")
	@PostMapping("/listByPage")
	public JsonResult listByPage(@RequestBody @ApiParam("品牌属性") BrandDto brandDto) throws Exception{
	    
		Page<PhBrand> pages = phBrandService.findByPage(brandDto, null, PageRequest.of(brandDto.getPage(), brandDto.getSize()));

		return jsonResultHelper.buildSuccessJsonResult(pages);
	}
	
	@ApiOperation("品牌详情")
	@GetMapping("/info")
	public JsonResult info(@ApiParam("品牌ID")@RequestParam Long id){
		return jsonResultHelper.buildSuccessJsonResult(phBrandService.getOne(id));
	}
	
	@ApiOperation("品牌详情")
	@GetMapping("/infoByName")
	public JsonResult infoByName(@ApiParam("品牌名称")@RequestParam String name){
		PhBrand phBrand = phBrandService.findByName(name);
		if(null == phBrand){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
		}
		return jsonResultHelper.buildSuccessJsonResult(phBrand);
	}
	
	
}
