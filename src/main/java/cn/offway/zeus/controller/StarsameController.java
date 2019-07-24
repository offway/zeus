package cn.offway.zeus.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import cn.offway.zeus.domain.PhStarsame;
import cn.offway.zeus.domain.PhStarsameGoods;
import cn.offway.zeus.repository.PhStarsameGoodsRepository;
import cn.offway.zeus.repository.PhStarsameImageRepository;
import cn.offway.zeus.repository.PhStarsameRepository;
import cn.offway.zeus.service.PhStarsameService;
import cn.offway.zeus.utils.CommonResultCode;
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
	
	@Autowired
	private PhStarsameRepository phStarsameRepository;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	private static final String STARSAME_PRAISE="zeus.starsame.praise";
	

	@ApiOperation("明星同款列表")
	@GetMapping("/list")
	public JsonResult list(
			@ApiParam("页码,从0开始") @RequestParam int page,
		    @ApiParam("页大小") @RequestParam int size,
		    @ApiParam("明星姓名") @RequestParam(required = false) String starName){
		
		return jsonResultHelper.buildSuccessJsonResult(phStarsameService.findByPage(starName,new PageRequest(page,size)));
	}
	
	@SuppressWarnings("unchecked")
	@ApiOperation("明星同款详情")
	@GetMapping("/info")
	public JsonResult info(
			@ApiParam("用户ID") @RequestParam(required = false) Long userId,
			@ApiParam("明星同款ID") @RequestParam Long id){
		
		Map<String, Object> resultMap = new HashMap<>();
		PhStarsame phStarsame = phStarsameService.getOne(id);
		resultMap = JSON.parseObject(JSON.toJSONString(phStarsame,SerializerFeature.WriteMapNullValue),Map.class);
		resultMap.put("banner", phStarsameImageRepository.findImageByStarsameIdOrderBySortAsc(id));
		
		List<PhStarsameGoods> list = phStarsameGoodsRepository.findByStarsameIdOrderByCreateTimeDesc(id);
		
		List<PhStarsameGoods> goods = new ArrayList<>();
		List<PhStarsameGoods> brands = new ArrayList<>();

		for (PhStarsameGoods phStarsameGoods : list) {
			if("1".equals(phStarsameGoods.getType())){
				brands.add(phStarsameGoods);
			}else{
				goods.add(phStarsameGoods);
			}
		}
		resultMap.put("goods",goods);
		resultMap.put("brands",brands);
		
		boolean praised = false;
		String starsamePraise = stringRedisTemplate.opsForValue().get(STARSAME_PRAISE+"_"+id+"_"+userId);
    	if(StringUtils.isNotBlank(starsamePraise)){
    		praised = true;
    	}
    	resultMap.put("praised",praised);
//    	resultMap.put("callCount",phStarsameRepository.countCall(id));
		return jsonResultHelper.buildSuccessJsonResult(resultMap);
	}
	
	@ApiOperation("明星同款点赞/取消点赞")
	@PostMapping("/praise")
	public JsonResult praise(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("明星同款ID") @RequestParam Long id){
		
		String starsamePraise = stringRedisTemplate.opsForValue().get(STARSAME_PRAISE+"_"+id+"_"+userId);
    	if(StringUtils.isBlank(starsamePraise)){
    		phStarsameService.praise(id);
			stringRedisTemplate.opsForValue().set(STARSAME_PRAISE+"_"+id+"_"+userId, ""+id);

    	}else{
    		phStarsameService.praisecancel(id);
			stringRedisTemplate.opsForValue().set(STARSAME_PRAISE+"_"+id+"_"+userId, "");
    	}

		return jsonResultHelper.buildSuccessJsonResult(null);
	}
	
	@ApiOperation("明星同款打call")
	@PostMapping("/call")
	public JsonResult call(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("明星同款ID") @RequestParam Long id){
		
		int count = phStarsameRepository.countCall(id, userId);
		if(count == 0){
			phStarsameRepository.insertCall(id, userId);
		}else{
			int c = phStarsameRepository.call(id, userId);
			if(c == 0){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.CALL_LIMIT);
			}
		}
		phStarsameRepository.addCall(id);
		return jsonResultHelper.buildSuccessJsonResult(null);
	}
	
	
}
