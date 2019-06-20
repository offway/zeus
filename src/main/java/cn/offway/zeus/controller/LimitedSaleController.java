package cn.offway.zeus.controller;

import java.util.Date;

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
		Page<PhLimitedSale> pages = phLimitedSaleService.findByPage(limitedSaleDto, new PageRequest(limitedSaleDto.getPage(), limitedSaleDto.getSize()));
		return jsonResultHelper.buildSuccessJsonResult(pages);
	}
	
	@ApiOperation("限量发售详情")
	@GetMapping("/info")
	public JsonResult info(
			@ApiParam("限量发售ID") @RequestParam Long id,
			@ApiParam("用户ID") @RequestParam(required = false) Long userId){
		PhLimitedSale phLimitedSale = phLimitedSaleService.findOne(id);
		LimitedSaleInfoDto dto = new LimitedSaleInfoDto();
		BeanUtils.copyProperties(phLimitedSale, dto);
		boolean assisted = false;
		if(null != userId){
			int c = phLimitedSaleOpRepository.countByLimitedSaleIdAndUserIdAndType(id, userId, "0");
			assisted = c>0;
		}
		dto.setAssisted(assisted);
		dto.setNow(new Date());
		
		return jsonResultHelper.buildSuccessJsonResult(dto);
	}
	
	@ApiOperation("好友助力/订阅")
	@PostMapping("/op")
	public JsonResult op(
			@ApiParam("限量发售ID") @RequestParam Long id,
			@ApiParam("用户ID") @RequestParam(required = false) Long userId,
			@ApiParam("类型[0-好友助力,1-订阅]") @RequestParam String type){
		int c = phLimitedSaleOpRepository.countByLimitedSaleIdAndUserIdAndType(id, userId, type);
		if(c == 0){
			PhLimitedSaleOp op = new PhLimitedSaleOp();
			op.setCreateTime(new Date());
			op.setLimitedSaleId(id);
			op.setType(type);
			op.setUserId(userId);
			phLimitedSaleOpRepository.save(op);
		}
		return jsonResultHelper.buildSuccessJsonResult(null);
	}
}
