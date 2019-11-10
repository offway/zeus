package cn.offway.zeus.controller;

import cn.offway.zeus.domain.*;
import cn.offway.zeus.dto.GoodsDto;
import cn.offway.zeus.dto.GoodsScreeningDto;
import cn.offway.zeus.dto.OrderInitDto;
import cn.offway.zeus.repository.PhPromotionInfoRepository;
import cn.offway.zeus.repository.PhVoucherProjectRepository;
import cn.offway.zeus.service.*;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

	@Autowired
	private PhPickService phPickService;

	@Autowired
	private VPickGoodsService vPickGoodsService;

	@Autowired
	private PhPromotionInfoRepository phPromotionInfoRepository;

	@Autowired
	private PhGoodsTypeService phGoodsTypeService;

	@Autowired
	private PhConfigService phConfigService;

	private static Date updateDate = null;



	@ApiOperation("查询商品类目")
	@GetMapping("/category")
	public JsonResult category(@ApiParam("商品类别") @RequestParam String type){
		return jsonResultHelper.buildSuccessJsonResult(phGoodsCategoryService.findByGoodsTypeNameOrderBySortAsc(type));
	}

	@ApiOperation("所有商品类目")
	@GetMapping("/categoryAll")
	public JsonResult categoryAll(){
		List<PhGoodsType> goodsType = phGoodsTypeService.findAll();
		List<Map> list = new ArrayList<>();
		for (PhGoodsType phGoodsType : goodsType) {
			Map<String,Object> map = new HashMap<>();
			List<PhGoodsCategory> goodsCategory = phGoodsCategoryService.findByGoodsTypeNameOrderBySortAsc(phGoodsType.getName());
			if (phGoodsType.getName() == "男装＆女装"){
				continue;
			}else {
				map.put("type",phGoodsType.getName());
				map.put("goodsCategory",goodsCategory);
				list.add(map);
			}
		}
		return jsonResultHelper.buildSuccessJsonResult(list);
	}

	@ApiOperation("商品列表")
	@PostMapping("/list")
	public JsonResult list(@RequestBody @ApiParam("商品属性") GoodsDto goodsDto ){

		//24小时随机排序
		Date now = new Date();
		if (null == updateDate || updateDate.before(DateUtils.addDays(now, -1))) {
			phGoodsService.updateSort();
			updateDate = now;
		}

		String sortDir = StringUtils.isBlank(goodsDto.getSortDir())?"desc":goodsDto.getSortDir();
		String sortName = StringUtils.isBlank(goodsDto.getSortName())?"sort":goodsDto.getSortName();

		PageRequest pageRequest = PageRequest.of(goodsDto.getPage(), goodsDto.getSize(),Direction.fromString(sortDir),sortName);
		if("saleCount".equals(sortName)){
			pageRequest = PageRequest.of(goodsDto.getPage(), goodsDto.getSize(),Direction.fromString(sortDir),"saleCount","sort");
		}

		Page<PhGoods> pages = phGoodsService.findByPage(goodsDto,pageRequest);

		return jsonResultHelper.buildSuccessJsonResult(pages);
	}

	@ApiOperation("商品sku")
	@GetMapping("/sku")
	public JsonResult sku(@ApiParam("商品ID") @RequestParam Long id){
		List<Map<String, Object>> list = new ArrayList<>();
		List<PhGoodsStock> phGoodsStocks = phGoodsStockService.findByGoodsId(id);
		List<PhGoodsProperty> phGoodsProperties = phGoodsPropertyService.findByGoodsId(id);

		for (PhGoodsStock phGoodsStock : phGoodsStocks) {
			Map<String, Object> map = new HashMap<>();
			Long stock = phGoodsStock.getStock();
			map.put("id", phGoodsStock.getId());
			map.put("stock", stock);
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
		return jsonResultHelper.buildSuccessJsonResult(list);
	}

	@ApiOperation("商品详情")
	@PostMapping("/info")
	public JsonResult info(@ApiParam("商品ID") @RequestParam Long id) throws Exception{

		Map<String, Object> resultMap = new HashMap<>();
		PhGoods phGoods = phGoodsService.findById(id);
		if("1".equals(phGoods.getLabel())){
			//限量商品不能查看详情
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.LIMITEDSALE_NOT_INFO);
		}
		List<String> banners = phGoodsImageService.findByGoodsId(id,"0");
		List<String> contents = phGoodsImageService.findByGoodsId(id,"1");

		List<Map<String, Object>> list = new ArrayList<>();
		List<PhGoodsStock> phGoodsStocks = phGoodsStockService.findByGoodsId(id);
		List<PhGoodsProperty> phGoodsProperties = phGoodsPropertyService.findByGoodsId(id);

		Long sumStock = 0L;
		for (PhGoodsStock phGoodsStock : phGoodsStocks) {
			Map<String, Object> map = new HashMap<>();
			Long stock = phGoodsStock.getStock();
			map.put("id", phGoodsStock.getId());
			map.put("stock", stock);
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
			sumStock +=stock;
		}

		resultMap.put("goods", phGoods);
		resultMap.put("banners", banners);
		resultMap.put("contents", contents);
		resultMap.put("skus", list);

		List<PhGoods> recommendGoods =  phGoodsService.findRecommend(id);
		resultMap.put("recommendGoods", recommendGoods);

		//查询改商品优惠券
		resultMap.put("voucherProjects", phVoucherProjectRepository.findByMerchantIdPublic(phGoods.getMerchantId()));

		//促销活动名称
		resultMap.put("promotionName", phPromotionInfoRepository.findNameByGoodsId(id));

		//是否售罄
		resultMap.put("sellOut",sumStock==0);
		//更新商品浏览量
		phGoodsService.updateViewCount(id);

		return jsonResultHelper.buildSuccessJsonResult(resultMap);
	}

	@ApiOperation("加/减购物车商品")
	@PostMapping("/shopingCar")
	public JsonResult shopingCar(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("商品库存ID") @RequestParam Long stockId,
			@ApiParam("数量") @RequestParam Long goodsCount,
			@ApiParam("类型[0-加,1-减,2-修改]") @RequestParam(required = false) String type){
		type = StringUtils.isBlank(type)?"0":type;//默认：加

		//限量商品不能添加购物车
		PhGoodsStock phGoodsStock = phGoodsStockService.findById(stockId);
		Long goodsId = phGoodsStock.getGoodsId();
		PhGoods phGoods = phGoodsService.findById(goodsId);
		if("1".equals(phGoods.getLabel())){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.LIMITEDSALE_NOT_CAR);
		}
		return phShoppingCartService.shopingCar(userId, stockId, goodsCount,type);
	}

	@ApiOperation("查看购物车")
	@GetMapping("/shopingCar")
	public JsonResult shopingCarList(@ApiParam("用户ID") @RequestParam Long userId){
		return phShoppingCartService.shopingCarList(userId);
	}

	@ApiOperation("查看购物车")
	@GetMapping("/shopingCarV2")
	public JsonResult shopingCarListV2(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("购物车ID")@RequestParam(required = false) List<Long> scIds ){
		scIds = null==scIds?new ArrayList<>():scIds;
		return phShoppingCartService.shopingCarListV2(userId,scIds);
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

	@ApiOperation("确认订单初始化校验")
	@PostMapping("/orderInitCheck")
	public JsonResult orderInitCheck(@RequestBody @ApiParam("请求参数") OrderInitDto orderInitDto){
		return phShoppingCartService.orderInitCheck(orderInitDto);

	}

	@ApiOperation("优选详情")
	@GetMapping("/pick/{id}")
	public JsonResult pickInfo(@ApiParam("优选ID") @PathVariable Long id){
		return jsonResultHelper.buildSuccessJsonResult(phPickService.findById(id));
	}

	@ApiOperation("优选商品列表")
	@PostMapping("/pick/list")
	public JsonResult pickgoods(
			@ApiParam("优选ID") @RequestParam Long pickId,
			@ApiParam("页码,从0开始") @RequestParam int page,
			@ApiParam("页大小") @RequestParam int size){
		return jsonResultHelper.buildSuccessJsonResult(vPickGoodsService.findByPage(pickId, PageRequest.of(page, size)));
	}

	@ApiOperation("商品筛选")
	@PostMapping("/screening")
	public JsonResult screening(@RequestBody @ApiParam("筛选条件") GoodsScreeningDto goodsScreeningDto) {
		if ((goodsScreeningDto.getSort() == 0 && goodsScreeningDto.getPickId() != null) && goodsScreeningDto.getSort() == 0){
			return jsonResultHelper.buildSuccessJsonResult(phGoodsService.limtbypick(goodsScreeningDto.getPickId(),(goodsScreeningDto.getPage()*10),goodsScreeningDto.getSize()));
		}else {
			return jsonResultHelper.buildSuccessJsonResult(phGoodsService.screening(goodsScreeningDto, PageRequest.of(goodsScreeningDto.getPage(), goodsScreeningDto.getSize())));
		}
	}

	@ApiOperation("筛选条件")
	@GetMapping("/screeningFilter")
	public <T> JsonResult screeningFilter() {
		Map<String, Object> map = new HashMap<>();
		List<Map> tag = new ArrayList<>();
		List<Map> priceAll = new ArrayList<>();
		Map<String, String> mapTag0 = new HashMap<>();
		/* 标签,该字段为二进制位运算标识,0否1是,从右到左第一位表示品牌保障,第二位表示7天退换货,第三位表示限量商品,第四位表示特殊商品。 **/
		mapTag0.put("tag", "1000");
		mapTag0.put("value", "特殊商品");
		Map<String, String> mapTag1 = new HashMap<>();
		mapTag1.put("tag", "0100");
		mapTag1.put("value", "限量商品");
		Map<String, String> mapTag2 = new HashMap<>();
		mapTag2.put("tag", "0001");
		mapTag2.put("value", "品牌保障");
		for (int i = 0; i < 1000; i += 100) {
			int max = i + 100;
			Map<String, Object> price = new HashMap<>();
			if (max < 1000) {
				if (i < 300) {
					price.put("priceMini", i);
					price.put("priceMax", max);
					price.put("value", ("¥" + i + "-" + max));
					priceAll.add(price);
				} else if (i == 300) {
					price.put("priceMini", i);
					price.put("priceMax", 500);
					price.put("value", ("¥" + i + "-" + 500));
					priceAll.add(price);
				} else if (i == 500) {
					price.put("priceMini", i);
					price.put("priceMax", 800);
					price.put("value", ("¥" + i + "-" + 800));
					priceAll.add(price);
				}
			} else {
				price.put("priceMini", 1000);
				price.put("priceMax", 9999999);
				price.put("value", "¥1000以上");
				priceAll.add(price);
			}
		}
		tag.add(mapTag0);
		tag.add(mapTag1);
		tag.add(mapTag2);
		List<Map> type0 = new ArrayList<>();
		PhConfig configType = phConfigService.findByName("INDEX_SEARCH");
		JSONArray jsonArray0 = JSON.parseArray(configType.getContent());
		for (Map o : jsonArray0.toJavaList(Map.class)) {
			Map<String,String> typeadd = new HashMap<>();
			typeadd.put("name",o.get("name").toString());
			typeadd.put("value",o.get("value").toString());
			type0.add(typeadd);
		}
		List<Object> style = new ArrayList<>();
		PhConfig config = phConfigService.findByName("INDEX_STYLE_FULL");
		JSONArray jsonArray = JSON.parseArray(config.getContent());
		for (Map m : jsonArray.toJavaList(Map.class)) {
			style.add(m.get("value"));
		}
		map.put("type", type0);
		map.put("tag", tag);
		map.put("style", style);
		map.put("price", priceAll);
		return jsonResultHelper.buildSuccessJsonResult(map);
	}
}
