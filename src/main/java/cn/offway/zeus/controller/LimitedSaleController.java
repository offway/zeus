package cn.offway.zeus.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhLimitedSale;
import cn.offway.zeus.dto.LimitedSaleDto;
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
	
	
	@ApiOperation("限量发售列表")
	@PostMapping("/list")
	public JsonResult list(@RequestBody @ApiParam("请求参数") LimitedSaleDto limitedSaleDto){
		Page<PhLimitedSale> pages = phLimitedSaleService.findByPage(limitedSaleDto, new PageRequest(limitedSaleDto.getPage(), limitedSaleDto.getSize()));
		return jsonResultHelper.buildSuccessJsonResult(pages);
	}
	
	@ApiOperation("限量发售详情")
	@PostMapping("/info")
	public JsonResult info(@ApiParam("限量发售ID") @RequestParam Long id){
		return jsonResultHelper.buildSuccessJsonResult(phLimitedSaleService.findOne(id));
	}
}
