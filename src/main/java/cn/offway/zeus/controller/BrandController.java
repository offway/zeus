package cn.offway.zeus.controller;

import java.util.ArrayList;
import java.util.List;

import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.dto.GIOEventMessage;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import cn.offway.zeus.domain.PhBrand;
import cn.offway.zeus.domain.PhGoods;
import cn.offway.zeus.dto.BrandDto;
import cn.offway.zeus.dto.BrandRecommendDto;
import cn.offway.zeus.service.PhBrandService;
import cn.offway.zeus.service.PhGoodsService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;

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
	public JsonResult<List<PhBrand>> list(
			@ApiParam("类型[0-国内品牌，1-国际品牌]") @RequestParam(required = false) String type){
		return jsonResultHelper.buildSuccessJsonResult(phBrandService.findAll(type));
	}
	
	@ApiOperation("推荐品牌")
	@PostMapping("/recommends")
	public JsonResult<Page<BrandRecommendDto>> recommends(@RequestBody @ApiParam("品牌属性") BrandDto brandDto) throws Exception{
	    
		Page<PhBrand> pages = phBrandService.findByPage(brandDto, "1", PageRequest.of(brandDto.getPage(), brandDto.getSize()));
		
		List<PhBrand> phBrands = pages.getContent();
		List<BrandRecommendDto> dtos = new ArrayList<>();
	
		for (PhBrand phBrand : phBrands) {
			BrandRecommendDto dto = new BrandRecommendDto();
			if(null!=phBrand){
				BeanUtils.copyProperties(phBrand, dto);
			}
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
	public JsonResult<Page<PhBrand>> listByPage(@RequestBody @ApiParam("品牌属性") BrandDto brandDto) throws Exception{
	    
		Page<PhBrand> pages = phBrandService.findByPage(brandDto, null, PageRequest.of(brandDto.getPage(), brandDto.getSize()));

		return jsonResultHelper.buildSuccessJsonResult(pages);
	}
	
	@ApiOperation("品牌详情")
	@GetMapping("/info")
	public JsonResult<PhBrand> info(@ApiParam(value ="品牌ID",required = true)@RequestParam Long id){
		return jsonResultHelper.buildSuccessJsonResult(phBrandService.findById(id));
	}

	@ApiOperation("品牌详情")
	@GetMapping("/infoByName")
	public JsonResult<PhBrand> infoByName(@ApiParam("品牌名称")@RequestParam String name){
		PhBrand phBrand = phBrandService.findByName(name);
		if(null == phBrand){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
		}
		GIOEventMessage eventMessage = new GIOEventMessage.Builder()
				.eventTime(System.currentTimeMillis())            // 事件时间，默认为系统时间（选填）
				.eventKey("brandDetails")                           // 事件标识 (必填)
				.loginUserId("18")                   // 登录用户ID (必填)
				.addEventVariable("BannerID", phBrand.getId().toString())    // 事件级变量 (选填)
				.build();
		//上传事件行为消息到服务器
		GrowingAPI.send(eventMessage);
		return jsonResultHelper.buildSuccessJsonResult(phBrand);
	}

	@ApiOperation("品牌新上前10")
	@GetMapping("/newtop10")
	public JsonResult<PhBrand> newtop(){
		List<PhBrand> phBrand = phBrandService.findNewTop10();
		return jsonResultHelper.buildSuccessJsonResult(phBrand);
	}
}
