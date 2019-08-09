package cn.offway.zeus.controller;

import java.text.SimpleDateFormat;
import java.util.*;

import cn.offway.zeus.utils.CommonResultCode;
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

import cn.offway.zeus.domain.PhLimitedSale;
import cn.offway.zeus.domain.PhLimitedSaleOp;
import cn.offway.zeus.dto.LimitedSaleDto;
import cn.offway.zeus.dto.LimitedSaleInfoDto;
import cn.offway.zeus.repository.PhGoodsStockRepository;
import cn.offway.zeus.repository.PhLimitedSaleOpRepository;
import cn.offway.zeus.service.PhLimitedSaleService;
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
	
	@ApiOperation("限量发售列表")
	@PostMapping("/list")
	public JsonResult list(@RequestBody @ApiParam("请求参数") LimitedSaleDto limitedSaleDto){
		Page<PhLimitedSale> pages = phLimitedSaleService.findByPage(limitedSaleDto, PageRequest.of(limitedSaleDto.getPage(), limitedSaleDto.getSize()));
		return jsonResultHelper.buildSuccessJsonResult(pages);
	}

	@ApiOperation("限量发售那个头")
	@GetMapping("/head")
	public JsonResult head(){
		Date now = new Date();
		List<Map<String,Object>> list = new ArrayList<>();
		List<PhLimitedSale> sales = phLimitedSaleService.findHead();
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
		for (PhLimitedSale sale : sales) {
			Map<String,Object> map = new HashMap<>();
			map.put("id",sale.getId());
			map.put("date",sdf.format(sale.getBeginTime()));
			map.put("text",now.before(sale.getBeginTime())?"即将发售":"进行中");
			list.add(map);
		}

		if(list.isEmpty()){
			PhLimitedSale phLimitedSale = phLimitedSaleService.findHeadForEnd();
			Map<String,Object> map = new HashMap<>();
			map.put("id",phLimitedSale.getId());
			map.put("date",sdf.format(phLimitedSale.getBeginTime()));
			map.put("text","已结束");
			list.add(map);
		}

		return jsonResultHelper.buildSuccessJsonResult(list);
	}
	
	@ApiOperation("限量发售详情")
	@GetMapping("/info")
	public JsonResult info(
			@ApiParam("限量发售ID") @RequestParam Long id,
			@ApiParam("用户ID") @RequestParam(required = false) Long userId){
		PhLimitedSale phLimitedSale = phLimitedSaleService.findById(id);
		LimitedSaleInfoDto dto = new LimitedSaleInfoDto();
		if(null!=phLimitedSale){
			BeanUtils.copyProperties(phLimitedSale, dto);
		}
		int currentCount = 0;
		if(null != userId){
			int c = phLimitedSaleOpRepository.countByLimitedSaleIdAndUserIdAndType(id,userId,"0");
			currentCount = c;
		}
		dto.setCurrentCount((long)currentCount);
		dto.setNow(new Date());

		return jsonResultHelper.buildSuccessJsonResult(dto);
	}
	
	@ApiOperation("好友助力/订阅")
	@PostMapping("/op")
	public JsonResult op(
			@ApiParam("限量发售ID") @RequestParam Long id,
			@ApiParam("分享用户ID") @RequestParam Long userId,
			@ApiParam("助力用户ID") @RequestParam Long boostUserId){

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
