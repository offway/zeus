package cn.offway.zeus.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import cn.offway.zeus.domain.PhCollect;
import cn.offway.zeus.domain.PhGoods;
import cn.offway.zeus.domain.PhGoodsProperty;
import cn.offway.zeus.domain.PhGoodsStock;
import cn.offway.zeus.dto.GoodsDto;
import cn.offway.zeus.dto.OrderInitDto;
import cn.offway.zeus.repository.PhVoucherProjectRepository;
import cn.offway.zeus.service.PhCollectService;
import cn.offway.zeus.service.PhGoodsCategoryService;
import cn.offway.zeus.service.PhGoodsImageService;
import cn.offway.zeus.service.PhGoodsPropertyService;
import cn.offway.zeus.service.PhGoodsService;
import cn.offway.zeus.service.PhGoodsStockService;
import cn.offway.zeus.service.PhShoppingCartService;
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
	
	@Autowired
	private PhGoodsPropertyService phGoodsPropertyService;
	
	@Autowired
	private PhShoppingCartService phShoppingCartService;
	
	@Autowired
	private PhVoucherProjectRepository phVoucherProjectRepository;
	
	
	
	@ApiOperation("查询商品类目")
	@GetMapping("/category")
	public JsonResult category(@ApiParam("商品类别") @RequestParam String type){
		return jsonResultHelper.buildSuccessJsonResult(phGoodsCategoryService.findByGoodsTypeNameOrderBySortAsc(type));
	}
	
	@ApiOperation("商品列表")
	@PostMapping("/list")
	public JsonResult list(@RequestBody @ApiParam("商品属性") GoodsDto goodsDto ){
		String sortDir = StringUtils.isBlank(goodsDto.getSortDir())?"desc":goodsDto.getSortDir();
		String sortName = StringUtils.isBlank(goodsDto.getSortName())?"upTime":goodsDto.getSortName();

		sortName = "createTime".equals(sortName)?"upTime":sortName;
		
		PageRequest pageRequest = new PageRequest(goodsDto.getPage(), goodsDto.getSize(),Direction.fromString(sortDir),sortName);
		if("saleCount".equals(sortName)){
			pageRequest = new PageRequest(goodsDto.getPage(), goodsDto.getSize(),Direction.fromString(sortDir),sortName,"upTime");
		}
		
		Page<PhGoods> pages = phGoodsService.findByPage(goodsDto,pageRequest);
		
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
		List<PhGoodsProperty> phGoodsProperties = phGoodsPropertyService.findByGoodsId(id);
		for (PhGoodsStock phGoodsStock : phGoodsStocks) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", phGoodsStock.getId());
			map.put("stock", phGoodsStock.getStock());
			map.put("img", phGoodsStock.getImage());
			map.put("price", phGoodsStock.getPrice());
			
			List<Map<String, Object>> attributes = new ArrayList<>();
			for (PhGoodsProperty phGoodsProperty : phGoodsProperties) {
				if(phGoodsProperty.getGoodsStockId().longValue()==phGoodsStock.getId().longValue()){
					Map<String, Object> attr = new HashMap<>();
					attr.put("key", phGoodsProperty.getName());
					attr.put("value", phGoodsProperty.getValue());
					attributes.add(attr);
				}
			}
			
			map.put("attributes",attributes );
			list.add(map);
		}
		
		resultMap.put("goods", phGoods);
		resultMap.put("banners", banners);
		resultMap.put("contents", contents);
		resultMap.put("skus", list);
		
		List<PhGoods> recommendGoods =  phGoodsService.findRecommend(id);
		resultMap.put("recommendGoods", recommendGoods);
		
		//查询改商品优惠券
		resultMap.put("voucherProjects", phVoucherProjectRepository.findByMerchantId(phGoods.getMerchantId()));
		
		//更新商品浏览量
		phGoodsService.updateViewCount(id);

		return jsonResultHelper.buildSuccessJsonResult(resultMap);
	}
	
	@ApiOperation("加入购物车")
	@PostMapping("/shopingCar")
	public JsonResult shopingCar(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("商品库存ID") @RequestParam Long stockId,
			@ApiParam("加入数量") @RequestParam Long goodsCount){
		
		return phShoppingCartService.shopingCar(userId, stockId, goodsCount);
	}
	
	@ApiOperation("查看购物车")
	@GetMapping("/shopingCar")
	public JsonResult shopingCarList(@ApiParam("用户ID") @RequestParam Long userId){
		return phShoppingCartService.shopingCarList(userId);
	}
	
	@ApiOperation("删除购物车")
	@DeleteMapping("/shopingCar")
	public JsonResult shopingCarDel(@ApiParam("购物车ID") @RequestParam List<Long> ids){
		phShoppingCartService.delete(ids);
		return jsonResultHelper.buildSuccessJsonResult(null);
	}
	
	@ApiOperation("确认订单初始化")
	@PostMapping("/orderInit")
	public JsonResult orderInit(@RequestBody @ApiParam("请求参数") OrderInitDto orderInitDto){
		return phShoppingCartService.orderInit(orderInitDto);

	}
	
}
