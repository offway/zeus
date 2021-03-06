package cn.offway.zeus.controller;

import java.text.SimpleDateFormat;
import java.util.*;

import cn.offway.zeus.domain.*;
import cn.offway.zeus.service.*;
import cn.offway.zeus.utils.CommonResultCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.dto.LimitedSaleDto;
import cn.offway.zeus.dto.LimitedSaleInfoDto;
import cn.offway.zeus.repository.PhGoodsStockRepository;
import cn.offway.zeus.repository.PhLimitedSaleOpRepository;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags={"限量发售"})
@RestController
@RequestMapping("/limitedSale")
public class LimitedSaleController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhLimitedSaleService phLimitedSaleService;
	
	@Autowired
	private PhLimitedSaleOpRepository phLimitedSaleOpRepository;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	private static final String SMS_CODE_KEY="zeus.sms.code";

	@Autowired
	private PhUserInfoService phUserInfoService;

	@Autowired
    private PhOrderGoodsService phOrderGoodsService;

	@Autowired
	private PhGoodsService phGoodsService;

	@Autowired
	private PhGoodsImageService phGoodsImageService;

	@Autowired
	private PhGoodsStockService phGoodsStockService;

	@Autowired
	private PhGoodsPropertyService phGoodsPropertyService;

	@Autowired
	private PhConfigService phConfigService;
	
	@ApiOperation("限量发售列表")
	@PostMapping("/list")
	public JsonResult list(@RequestBody @ApiParam("请求参数") LimitedSaleDto limitedSaleDto){
		Page<PhLimitedSale> pages = phLimitedSaleService.findByPage(limitedSaleDto, PageRequest.of(limitedSaleDto.getPage(), limitedSaleDto.getSize()));
		return jsonResultHelper.buildSuccessJsonResult(pages);
	}

	@ApiOperation("限量发售那个头")
	@GetMapping("/head")
	public JsonResult head(@ApiParam("渠道,该字段为二进制位运算标识,0否1是,从右到左第一位表示H5,第二位表示小程序,第三位表示APP。如要查询APP则传参为0100,查询H5和小程序则传参0011以此类推") @RequestParam(defaultValue = "0100") String channel){

		int channelInt = Integer.parseInt(channel,2);
		Date now = new Date();
		List<Map<String,Object>> list = new ArrayList<>();
		List<PhLimitedSale> sales = phLimitedSaleService.findHead(channelInt);
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
		for (PhLimitedSale sale : sales) {
			Map<String,Object> map = new HashMap<>();
			map.put("id",sale.getId());
			map.put("date",sdf.format(sale.getBeginTime()));
			map.put("text",now.before(sale.getBeginTime())?"即将发售":"进行中");
			map.put("isShow",sale.getIsShow());
			list.add(map);
		}

		if(list.isEmpty()){
			PhLimitedSale phLimitedSale = phLimitedSaleService.findHeadForEnd(channelInt);
			Map<String,Object> map = new HashMap<>();
			map.put("id",phLimitedSale.getId());
			map.put("date",sdf.format(phLimitedSale.getBeginTime()));
			map.put("text","已结束");
			map.put("isShow",phLimitedSale.getIsShow());
			list.add(map);
		}

		return jsonResultHelper.buildSuccessJsonResult(list);
	}
	
	@ApiOperation("限量发售详情")
	@GetMapping("/info")
	public JsonResult<LimitedSaleInfoDto> info(
			@ApiParam("限量发售ID") @RequestParam Long id,
			@ApiParam("用户ID") @RequestParam(required = false) Long userId){
		PhLimitedSale phLimitedSale = phLimitedSaleService.findById(id);
		LimitedSaleInfoDto dto = new LimitedSaleInfoDto();
		if(null!=phLimitedSale){
			BeanUtils.copyProperties(phLimitedSale, dto);
		}
		int currentCount = 0;//当前助力次数
		if(null != userId){
			int c = phLimitedSaleOpRepository.countByLimitedSaleIdAndUserIdAndType(id,userId,"0");
			currentCount = c;
		}
		dto.setCurrentCount((long)currentCount);
		//查询该商品已购买数量
        int buyCount = 0;
        if(null != userId){
			buyCount = phOrderGoodsService.sumGoodsCountByLimitSale(phLimitedSale.getGoodsId(),userId,phLimitedSale.getBeginTime(),phLimitedSale.getEndTime());
        }
        dto.setBuyCount((long)buyCount);
		dto.setNow(new Date());

		return jsonResultHelper.buildSuccessJsonResult(dto);
	}

	@ApiOperation("商品详情")
	@PostMapping("/goods/info")
	public JsonResult goodsInfo(
			@ApiParam("限量发售ID") @RequestParam Long lsId,
			@ApiParam("用户ID") @RequestParam Long userId) throws Exception{

		Map<String, Object> resultMap = new HashMap<>();


		PhLimitedSale phLimitedSale = phLimitedSaleService.findById(lsId);
		if(null == phLimitedSale){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
		}

		int currentCount = 0;//当前助力次数
		if(null != userId){
			int c = phLimitedSaleOpRepository.countByLimitedSaleIdAndUserIdAndType(lsId,userId,"0");
			currentCount = c;
		}

		//是否助力成功
		boolean isBoost = currentCount >= phLimitedSale.getBoostCount();
		resultMap.put("isBoost",isBoost);

		//查询该用户该商品已购买数量
		int buyCount = 0;
		if(null != userId){
			buyCount = phOrderGoodsService.sumGoodsCountByLimitSale(phLimitedSale.getGoodsId(),userId,phLimitedSale.getBeginTime(),phLimitedSale.getEndTime());
		}
		resultMap.put("canBuyCount",phLimitedSale.getBuyLimit().intValue() - buyCount);


		Long id = phLimitedSale.getGoodsId();
		PhGoods phGoods = phGoodsService.findById(id);

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

		//是否售罄
		resultMap.put("sellOut",sumStock==0);


		return jsonResultHelper.buildSuccessJsonResult(resultMap);
	}

	@ApiOperation("批量助力")
	@PostMapping("/op/batch")
	public JsonResult opBatch(
		    @ApiParam("分享用户ID") @RequestParam Long userId,
			@ApiParam("助力用户手机号") @RequestParam String phone,
			@ApiParam("助力用户验证码") @RequestParam String code,
			@ApiParam("配置") @RequestParam String config){

		String content = phConfigService.findContentByName(config);
		String [] ids = content.split(",");

		if(StringUtils.isBlank(phone)){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_MISS);
		}

		phone = phone.contains("+")?phone:"+86"+phone;

		String smsCode = stringRedisTemplate.opsForValue().get(SMS_CODE_KEY+"_"+phone);
		if(StringUtils.isBlank(smsCode)){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_INVALID);
		}

		if(!code.equals(smsCode)){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_ERROR);
		}


		PhUserInfo phUserInfo = phUserInfoService.findByPhone(phone);


		PhLimitedSale phLimitedSale = phLimitedSaleService.findById(Long.parseLong(ids[0]));

		//用户类型[0-新用户,1-老用户]
		String userType = phLimitedSale.getUserType();
		if("0".equals(userType)){
			if(null!=phUserInfo){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_EXISTS);
			}
		}

		if(null==phUserInfo){
			phUserInfo = phUserInfoService.register(phone,null,null,null,null,null,null,null,null);
		}

		Long boostUserId = phUserInfo.getId();

		if(userId.longValue()==boostUserId.longValue()){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.FREE_BOOST_MY);
		}



		phLimitedSaleOpRepository.boostBatch(userId,Arrays.asList(ids),boostUserId);
		return jsonResultHelper.buildSuccessJsonResult(null);
	}
	
	@ApiOperation("好友助力")
	@PostMapping("/op")
	public JsonResult op(
			@ApiParam("限量发售ID") @RequestParam Long id,
			@ApiParam("分享用户ID") @RequestParam Long userId,
			@ApiParam("助力用户手机号") @RequestParam String phone,
			@ApiParam("助力用户验证码") @RequestParam String code){


		PhLimitedSale phLimitedSale = phLimitedSaleService.findById(id);

		if(null == phLimitedSale){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
		}

		if(StringUtils.isBlank(phone)){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_MISS);
		}

		phone = phone.contains("+")?phone:"+86"+phone;

		String smsCode = stringRedisTemplate.opsForValue().get(SMS_CODE_KEY+"_"+phone);
		if(StringUtils.isBlank(smsCode)){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_INVALID);
		}

		if(!code.equals(smsCode)){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_ERROR);
		}


		PhUserInfo phUserInfo = phUserInfoService.findByPhone(phone);

		//用户类型[0-新用户,1-老用户]
		String userType = phLimitedSale.getUserType();
		if("0".equals(userType)){
			if(null!=phUserInfo){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_EXISTS);
			}
		}

		if(null==phUserInfo){
			phUserInfo = phUserInfoService.register(phone,null,null,null,null,null,null,null,null);
		}

		Long boostUserId = phUserInfo.getId();



		if(userId.longValue()==boostUserId.longValue()){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.FREE_BOOST_MY);

		}
		int c = phLimitedSaleOpRepository.countByLimitedSaleIdAndUserIdAndTypeAndBoostUserId(id, userId, "0",boostUserId);
		if(c == 0){
			PhLimitedSaleOp op = new PhLimitedSaleOp();
			op.setCreateTime(new Date());
			op.setLimitedSaleId(id);
			op.setType("0");
			op.setUserId(userId);
			op.setBoostUserId(boostUserId);
			phLimitedSaleOpRepository.save(op);
			return jsonResultHelper.buildSuccessJsonResult(null);
		}else{
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.FREE_BOOSTED);
		}
	}
}
