package cn.offway.zeus.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhGoods;
import cn.offway.zeus.domain.PhGoodsStock;
import cn.offway.zeus.dto.GoodsDto;
import cn.offway.zeus.service.PhGoodsCategoryService;
import cn.offway.zeus.service.PhGoodsImageService;
import cn.offway.zeus.service.PhGoodsService;
import cn.offway.zeus.service.PhGoodsStockService;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags={"商品"})
@RestController
@RequestMapping("/goods")
public class GoodsController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhGoodsService phGoodsService;
	
	@Autowired
	private PhGoodsImageService phGoodsImageService;
	
	@Autowired
	private PhGoodsStockService phGoodsStockService;
	
	@Autowired
	private PhGoodsCategoryService phGoodsCategoryService;
	
	
	@ApiOperation("查询商品类目")
	@GetMapping("/category")
	public JsonResult category(@ApiParam("商品类别") @RequestParam String type){
		return jsonResultHelper.buildSuccessJsonResult(phGoodsCategoryService.findByGoodsTypeNameOrderBySortAsc(type));
	}
	
	@ApiOperation("商品列表")
	@PostMapping("/list")
	public JsonResult list(@RequestBody @ApiParam("商品属性") GoodsDto goodsDto ){
		String sortDir = StringUtils.isBlank(goodsDto.getSortDir())?"desc":goodsDto.getSortDir();
		String sortName = StringUtils.isBlank(goodsDto.getSortName())?"id":goodsDto.getSortName();

		Page<PhGoods> pages = phGoodsService.findByPage(goodsDto, new PageRequest(goodsDto.getPage(), goodsDto.getSize(),Direction.fromString(sortDir),sortName));
		
		return jsonResultHelper.buildSuccessJsonResult(pages);
	}
	
	@ApiOperation("商品详情")
	@PostMapping("/info")
	public JsonResult info(@ApiParam("商品ID") @RequestParam Long id) throws Exception{
		
		Map<String, Object> resultMap = new HashMap<>();
		PhGoods phGoods = phGoodsService.findOne(id);
		List<String> banners = phGoodsImageService.findByGoodsId(id,"0");
		List<String> contents = phGoodsImageService.findByGoodsId(id,"1");

		List<Map<String, Object>> list = new ArrayList<>();
		List<PhGoodsStock> phGoodsStocks = phGoodsStockService.findByGoodsId(id);
		Set<String> sizes = new HashSet<>();
		Set<String> colors = new HashSet<>();
		for (PhGoodsStock phGoodsStock : phGoodsStocks) {
			Map<String, Object> map = new HashMap<>();
			map.put("size", phGoodsStock.getSize());
			map.put("color", phGoodsStock.getColor());
			map.put("stock", phGoodsStock.getStock());
			map.put("img", phGoodsStock.getImage());
			list.add(map);
			sizes.add(phGoodsStock.getSize());
			colors.add(phGoodsStock.getColor());
			
		}
		
		resultMap.put("goods", phGoods);
		resultMap.put("banners", banners);
		resultMap.put("contents", contents);
		
		
		Map<String, Object> stock = new HashMap<>();
		stock.put("sizes", sizes);
		stock.put("colors", colors);
		stock.put("details", list);
		resultMap.put("stock", stock);
		
		List<PhGoods> recommendGoods =  phGoodsService.findRecommend(id);
		resultMap.put("recommendGoods", recommendGoods);

		return jsonResultHelper.buildSuccessJsonResult(resultMap);
	}
	
	
}
