package cn.offway.zeus.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import cn.offway.zeus.domain.PhStarsame;
import cn.offway.zeus.repository.PhStarsameGoodsRepository;
import cn.offway.zeus.repository.PhStarsameImageRepository;
import cn.offway.zeus.service.PhStarsameService;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags={"明星同款"})
@RestController
@RequestMapping("/starsame")
public class StarsameController {

	
	@Autowired
	private PhStarsameService phStarsameService;
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhStarsameImageRepository phStarsameImageRepository;
	
	@Autowired
	private PhStarsameGoodsRepository phStarsameGoodsRepository;
	

	@ApiOperation("明星同款列表")
	@GetMapping("/list")
	public JsonResult list(
			@ApiParam("页码,从0开始") @RequestParam int page,
		    @ApiParam("页大小") @RequestParam int size){
		
		return jsonResultHelper.buildSuccessJsonResult(phStarsameService.findByPage(new PageRequest(page,size)));
	}
	
	@SuppressWarnings("unchecked")
	@ApiOperation("明星同款详情")
	@GetMapping("/info")
	public JsonResult info(@ApiParam("明星同款ID") @RequestParam Long id){
		
		Map<String, Object> resultMap = new HashMap<>();
		PhStarsame phStarsame = phStarsameService.findOne(id);
		resultMap = JSON.parseObject(JSON.toJSONString(phStarsame,SerializerFeature.WriteMapNullValue),Map.class);
		resultMap.put("banner", phStarsameImageRepository.findImageByStarsameIdOrderBySortAsc(id));
		resultMap.put("goods",phStarsameGoodsRepository.findByStarsameIdOrderByCreateTimeDesc(id));
		return jsonResultHelper.buildSuccessJsonResult(resultMap);
	}
	
	@ApiOperation("明星同款点赞")
	@PostMapping("/praise")
	public JsonResult praise(@ApiParam("明星同款ID") @RequestParam Long id){
		phStarsameService.praise(id);
		return jsonResultHelper.buildSuccessJsonResult(null);
	}
	
	
}
