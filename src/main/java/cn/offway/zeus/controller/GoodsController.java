package cn.offway.zeus.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhGoods;
import cn.offway.zeus.dto.GoodsDto;
import cn.offway.zeus.service.PhGoodsService;
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
	
	
	@ApiOperation("商品列表")
	@PostMapping("/list")
	public JsonResult list(@RequestBody @ApiParam("商品属性") GoodsDto goodsDto ){
		
		Page<PhGoods> pages = phGoodsService.findByPage(goodsDto, new PageRequest(goodsDto.getPage(), goodsDto.getSize()));
		
		return jsonResultHelper.buildSuccessJsonResult(pages);
	}
}
