package cn.offway.zeus.controller;

import cn.offway.zeus.domain.PhStarsame;
import cn.offway.zeus.domain.PhStarsameComments;
import cn.offway.zeus.domain.PhStarsameGoods;
import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.repository.PhStarsameGoodsRepository;
import cn.offway.zeus.repository.PhStarsameImageRepository;
import cn.offway.zeus.repository.PhStarsameRepository;
import cn.offway.zeus.service.PhStarsameCommentsService;
import cn.offway.zeus.service.PhStarsameService;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
	private PhStarsameCommentsService starsameCommentsService;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private PhUserInfoService userInfoService;
	
	private static final String STARSAME_PRAISE="zeus.starsame.praise";
	

	@ApiOperation("明星同款列表")
	@GetMapping("/list")
	public JsonResult list(
			@ApiParam("页码,从0开始") @RequestParam int page,
		    @ApiParam("页大小") @RequestParam int size,
		    @ApiParam("明星姓名") @RequestParam(required = false) String starName,
			@ApiParam("用户ID") @RequestParam(required = false) Long userid,
			@ApiParam("排序字段[sort-APP,sortMini-小程序]") @RequestParam(defaultValue = "sort") String sortName){
		Page<PhStarsame> phStarsames = phStarsameService.findByPage(starName,PageRequest.of(page,size, Sort.by(Sort.Order.asc(sortName),Sort.Order.desc("createTime"))));
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String,Object> map = new HashMap<>();
		List<Object> objectList = new ArrayList<>();
		for (PhStarsame phStarsame : phStarsames) {
			if(phStarsame.getSort() == 999) {
				if (null != userid) {
					String starsamePraise = stringRedisTemplate.opsForValue().get(STARSAME_PRAISE + "_" + phStarsame.getId() + "_" + userid);
					Map<String, Object> map1 = objectMapper.convertValue(phStarsame, Map.class);
					if (StringUtils.isBlank(starsamePraise)) {
						map1.put("praise", "0");
					} else {
						map1.put("praise", "1");
					}
					objectList.add(map1);
				} else {
					objectList.add(phStarsame);
				}
			}
		}
		map.put("content",objectList);
		return jsonResultHelper.buildSuccessJsonResult(map);
	}
	
	@SuppressWarnings("unchecked")
	@ApiOperation("明星同款详情")
	@GetMapping("/info")
	public JsonResult info(
			@ApiParam("用户ID") @RequestParam(required = false) Long userId,
			@ApiParam("明星同款ID") @RequestParam Long id){
		
		Map<String, Object> resultMap = new HashMap<>();
		PhStarsame phStarsame = phStarsameService.findById(id);
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

	@ApiOperation("明星同款顶部滑动")
	@GetMapping("/banner")
	public JsonResult banner(
			@ApiParam("用户ID") @RequestParam(required = false) Long userid) {
		Map<String, Object> map = new HashMap<>();
		List<PhStarsame> phStarsames = phStarsameService.indexData();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Object> objectList = new ArrayList<>();
		for (PhStarsame phStarsame : phStarsames) {
			if (null != userid) {
				String starsamePraise = stringRedisTemplate.opsForValue().get(STARSAME_PRAISE + "_" + phStarsame.getId() + "_" + userid);
				Map<String,Object> map1 = objectMapper.convertValue(phStarsame,Map.class);
				if (StringUtils.isBlank(starsamePraise)) {
					map1.put("praise","0");
				} else {
					map1.put("praise","1");
				}
				objectList.add(map1);
			}else {
				objectList.add(phStarsame);
			}
		}
		map.put("content",objectList);
		return jsonResultHelper.buildSuccessJsonResult(map);
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

	@ApiOperation("明星同款评论列表")
	@GetMapping("/commentList")
	public JsonResult commentList(
			@ApiParam("页码,从0开始") @RequestParam int page,
			@ApiParam("页大小") @RequestParam int size,
			@ApiParam("明星同款ID") @RequestParam Long starSameId) {
		return jsonResultHelper.buildSuccessJsonResult(starsameCommentsService.findByPage(starSameId, PageRequest.of(page, size, Sort.by(Sort.Order.desc("createTime")))));
	}

	@ApiOperation("明星同款评论")
	@PostMapping("/doComment")
	public JsonResult doComment(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("明星同款ID") @RequestParam Long starSameId,
			@ApiParam("评论内容") @RequestParam String content) {
		PhUserInfo userInfo = userInfoService.findById(userId);
		PhStarsameComments comments = new PhStarsameComments();
		comments.setUserId(userId);
		comments.setStarsameId(starSameId);
		comments.setContent(content);
		comments.setCreateTime(new Date());
		comments.setStatus("0");
		comments.setHeadimgurl(userInfo.getHeadimgurl());
		comments.setNickname(userInfo.getNickname());
		comments.setSex(userInfo.getSex());
		starsameCommentsService.save(comments);
		return jsonResultHelper.buildSuccessJsonResult(null);
	}
}
