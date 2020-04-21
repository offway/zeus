package cn.offway.zeus.controller;

import cn.offway.zeus.domain.*;
import cn.offway.zeus.repository.PhStarsameGoodsRepository;
import cn.offway.zeus.repository.PhStarsameImageRepository;
import cn.offway.zeus.repository.PhStarsameRepository;
import cn.offway.zeus.service.*;
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

@Api(tags = {"明星同款"})
@RestController
@RequestMapping("/starsame")
public class StarsameController {


    private static final String STARSAME_PRAISE = "zeus.starsame.praise";
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
    @Autowired
    private PhFollowService phFollowService;
    @Autowired
    private PhCelebrityListService phCelebrityListService;

    @ApiOperation("明星同款列表")
    @GetMapping("/list")
    public JsonResult list(
            @ApiParam("页码,从0开始") @RequestParam int page,
            @ApiParam("页大小") @RequestParam int size,
            @ApiParam("明星姓名") @RequestParam(required = false) String starName,
            @ApiParam("用户ID") @RequestParam(required = false) Long userid,
            @ApiParam("排序字段[sort-APP,sortMini-小程序]") @RequestParam(defaultValue = "sort") String sortName) {
        Page<PhStarsame> phStarsames = phStarsameService.findByPage(starName, PageRequest.of(page, size, Sort.by(Sort.Order.asc(sortName), Sort.Order.desc("createTime"))));
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>();
        List<Object> objectList = new ArrayList<>();
        for (PhStarsame phStarsame : phStarsames) {
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
        map.put("content", objectList);
        return jsonResultHelper.buildSuccessJsonResult(map);
    }

    @SuppressWarnings("unchecked")
    @ApiOperation("明星同款详情")
    @GetMapping("/info")
    public JsonResult info(
            @ApiParam("用户ID") @RequestParam(required = false) Long userId,
            @ApiParam("unionid") @RequestParam(required = false) String unionid,
            @ApiParam("明星同款ID") @RequestParam Long id,
            @ApiParam("是否加浏览数") @RequestParam(required = false, defaultValue = "0") int plusOne) {

        if (plusOne > 0) {
            phStarsameRepository.addView(id);
        }
        Map<String, Object> resultMap = new HashMap<>();
        PhStarsame phStarsame = phStarsameService.findById(id);
        resultMap = JSON.parseObject(JSON.toJSONString(phStarsame, SerializerFeature.WriteMapNullValue), Map.class);
        resultMap.put("banner", phStarsameImageRepository.findImageByStarsameIdOrderBySortAsc(id));

        List<PhStarsameGoods> list = phStarsameGoodsRepository.findByStarsameIdOrderByCreateTimeDesc(id);

        List<PhStarsameGoods> goods = new ArrayList<>();
        List<PhStarsameGoods> brands = new ArrayList<>();

        for (PhStarsameGoods phStarsameGoods : list) {
            if ("1".equals(phStarsameGoods.getType())) {
                brands.add(phStarsameGoods);
            } else {
                goods.add(phStarsameGoods);
            }
        }
        resultMap.put("goods", goods);
        resultMap.put("brands", brands);

        boolean praised = false;
        String starsamePraise = stringRedisTemplate.opsForValue().get(STARSAME_PRAISE + "_" + id + "_" + userId);
        if (StringUtils.isNotBlank(starsamePraise)) {
            praised = true;
        }
        List<Object> objectList = new ArrayList<>();
        List<PhCelebrityList> phCelebrityList = phCelebrityListService.findByName(phStarsame.getStarName());
        if (!"".equals(unionid)) {
            List<PhFollow> follow = phFollowService.findByUnionid(unionid);
            List<Long> list1 = new ArrayList<>();
            for (PhFollow phFollow : follow) {
                list1.add(phFollow.getCelebrityId());
            }

            for (PhCelebrityList celebrityList : phCelebrityList) {
                if (list1.contains(celebrityList.getId())) {
                    celebrityList.setFollow("Y");
                    objectList.add(celebrityList);
                } else {
                    celebrityList.setFollow("N");
                    objectList.add(celebrityList);
                }
            }
        } else {
            for (PhCelebrityList celebrityList : phCelebrityList) {
                celebrityList.setFollow("N");
                objectList.add(celebrityList);
            }
        }
        resultMap.put("celebrity", objectList);
        resultMap.put("praised", praised);
//    	resultMap.put("callCount",phStarsameRepository.countCall(id));
        return jsonResultHelper.buildSuccessJsonResult(resultMap);
    }

    @ApiOperation("明星同款点赞/取消点赞")
    @PostMapping("/praise")
    public JsonResult praise(
            @ApiParam("用户ID") @RequestParam Long userId,
            @ApiParam("明星同款ID") @RequestParam Long id) {

        String starsamePraise = stringRedisTemplate.opsForValue().get(STARSAME_PRAISE + "_" + id + "_" + userId);
        if (StringUtils.isBlank(starsamePraise)) {
            phStarsameService.praise(id);
            stringRedisTemplate.opsForValue().set(STARSAME_PRAISE + "_" + id + "_" + userId, "" + id);

        } else {
            phStarsameService.praisecancel(id);
            stringRedisTemplate.opsForValue().set(STARSAME_PRAISE + "_" + id + "_" + userId, "");
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
        map.put("content", objectList);
        return jsonResultHelper.buildSuccessJsonResult(map);
    }

    @ApiOperation("明星同款打call")
    @PostMapping("/call")
    public JsonResult call(
            @ApiParam("用户ID") @RequestParam Long userId,
            @ApiParam("明星同款ID") @RequestParam Long id) {

        int count = phStarsameRepository.countCall(id, userId);
        if (count == 0) {
            phStarsameRepository.insertCall(id, userId);
        } else {
            int c = phStarsameRepository.call(id, userId);
            if (c == 0) {
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

    @ApiOperation("添加关注")
    @PostMapping("/addfollow")
    public JsonResult addFollow(
            @ApiParam("unionid") @RequestParam String unionid,
            @ApiParam("明星Id") @RequestParam Long id) {
        PhFollow follows = phFollowService.findByUnionidAndCelebrityId(unionid, id);
        if ("".equals(unionid)){
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
        }
        if (null != follows) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.CELEBRITY_FOLLOW);
        }
        PhCelebrityList celebrity = phCelebrityListService.findOne(id);
        PhUserInfo user = userInfoService.findByUnionid(unionid);
        PhFollow follow = new PhFollow();
        follow.setUserId(user.getId());
        follow.setUnionid(user.getUnionid());
        follow.setNickname(user.getNickname());
        follow.setHeadimgurl(user.getHeadimgurl());
        follow.setCelebrityHeadimgurl(celebrity.getHeadimgurl());
        follow.setCelebrityId(celebrity.getId());
        follow.setCelebrityName(celebrity.getName());
        if ("".equals(celebrity.getFansSum()) || celebrity.getFansSum() == null) {
            celebrity.setFansSum(1L);
        } else {
            celebrity.setFansSum(celebrity.getFansSum() + 1L);
        }
        phFollowService.save(follow);
        phCelebrityListService.save(celebrity);
        return jsonResultHelper.buildSuccessJsonResult(CommonResultCode.SUCCESS);
    }

    @ApiOperation("取消关注")
    @PostMapping("/delfollow")
    public JsonResult delFollow(
            @ApiParam("unionid") @RequestParam String unionid,
            @ApiParam("明星Id") @RequestParam Long id) {
        PhFollow follows = phFollowService.findByUnionidAndCelebrityId(unionid, id);
        if ("".equals(unionid)){
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
        }
        if (null == follows) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.CELEBRITY_DELFOLLOW);
        }
        PhCelebrityList celebrity = phCelebrityListService.findOne(id);
        celebrity.setFansSum(celebrity.getFansSum() - 1L);
        phCelebrityListService.save(celebrity);
        phFollowService.deleteByUidAndCelebrityid(unionid, id);
        return jsonResultHelper.buildSuccessJsonResult(CommonResultCode.SUCCESS);
    }

    @ApiOperation("搜索明星")
    @GetMapping("/search")
    public JsonResult search(
            @ApiParam("unionid") @RequestParam String unionid,
            @ApiParam("name") @RequestParam String name) {
        List<PhFollow> follow = phFollowService.findByUnionid(unionid);
        List<PhCelebrityList> phCelebrityList = phCelebrityListService.findBynNameLike(name);
        List<PhCelebrityList> phCelebrityLists = new ArrayList<>();
        if (follow.size() <= 0) {
            for (PhCelebrityList celebrityList : phCelebrityList) {
                celebrityList.setFansSum(celebrityList.getFansSum()+celebrityList.getFans());
                celebrityList.setFollow("N");
                phCelebrityLists.add(celebrityList);
            }
        } else {
            List<Long> list = new ArrayList<>();
            for (PhFollow phFollow : follow) {
                list.add(phFollow.getCelebrityId());
            }
            for (PhCelebrityList celebrityList : phCelebrityList) {
                celebrityList.setFansSum(celebrityList.getFansSum()+celebrityList.getFans());
                if (list.contains(celebrityList.getId())) {
                    celebrityList.setFollow("Y");
                    phCelebrityLists.add(celebrityList);
                } else {
                    celebrityList.setFollow("N");
                    phCelebrityLists.add(celebrityList);
                }
            }
        }
        return jsonResultHelper.buildSuccessJsonResult(phCelebrityLists);
    }

    @ApiOperation("明星列表")
    @GetMapping("/starlist")
    public JsonResult starList(
            @ApiParam("unionid") @RequestParam String unionid,
            @ApiParam("页码,从0开始") @RequestParam Long page,
            @ApiParam("页大小") @RequestParam Long size) {
        List<PhFollow> follow = phFollowService.findByUnionid(unionid);
        List<PhCelebrityList> phCelebrityList = phCelebrityListService.findPage(page, size);
        List<PhCelebrityList> phCelebrityLists = new ArrayList<>();
        if (follow.size() <= 0) {
            for (PhCelebrityList celebrityList : phCelebrityList) {
                celebrityList.setFansSum(celebrityList.getFansSum()+celebrityList.getFans());
                celebrityList.setFollow("N");
                phCelebrityLists.add(celebrityList);
            }
        } else {
            List<Long> list = new ArrayList<>();
            for (PhFollow phFollow : follow) {
                list.add(phFollow.getCelebrityId());
            }
            for (PhCelebrityList celebrityList : phCelebrityList) {
                celebrityList.setFansSum(celebrityList.getFansSum()+celebrityList.getFans());
                if (list.contains(celebrityList.getId())) {
                    celebrityList.setFollow("Y");
                    phCelebrityLists.add(celebrityList);
                } else {
                    celebrityList.setFollow("N");
                    phCelebrityLists.add(celebrityList);
                }
            }

        }
        return jsonResultHelper.buildSuccessJsonResult(phCelebrityLists);
    }

    @ApiOperation("我的关注列表")
    @GetMapping("/myfollow")
    public JsonResult myfollow(
            @ApiParam("unionid") @RequestParam String unionid) {
        List<PhFollow> phFollows = phFollowService.findByUnionid(unionid);
        if (phFollows.size() <= 0) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.NOT_FOLLOW);
        }
        List<Long> list = new ArrayList<>();
        List<PhCelebrityList> phCelebrityLists = new ArrayList<>();
        for (PhFollow phFollow : phFollows) {
            PhCelebrityList celebrityList = phCelebrityListService.findOne(phFollow.getCelebrityId());
            celebrityList.setFollow("Y");
            celebrityList.setFansSum(celebrityList.getFansSum()+celebrityList.getFans());
            phCelebrityLists.add(celebrityList);
        }
        return jsonResultHelper.buildSuccessJsonResult(phCelebrityLists);
    }

    @ApiOperation("明星主页")
    @GetMapping("/starinfo")
    public JsonResult starinfo(
            @ApiParam("明星ID") @RequestParam Long id,
            @ApiParam("页码,从0开始") @RequestParam int page,
            @ApiParam("页大小") @RequestParam int size,
            @ApiParam("排序字段[sort-APP,sortMini-小程序]") @RequestParam(defaultValue = "sort") String sortName,
            @ApiParam("unionid") @RequestParam String unionid) {
        PhFollow follow = phFollowService.findByUnionidAndCelebrityId(unionid, id);
        Map<String, Object> map = new HashMap<>();
        PhCelebrityList celebrityList = phCelebrityListService.findOne(id);
        if (follow != null) {
            celebrityList.setFollow("Y");
        } else {
            celebrityList.setFollow("N");
        }
        celebrityList.setFansSum(celebrityList.getFansSum()+celebrityList.getFans());
        Page<PhStarsame> phStarsames = phStarsameService.findByPage(celebrityList.getName(), PageRequest.of(page, size, Sort.by(Sort.Order.asc(sortName), Sort.Order.desc("createTime"))));
        ObjectMapper objectMapper = new ObjectMapper();
        List<Object> objectList = new ArrayList<>();
        for (PhStarsame phStarsame : phStarsames) {
            List<PhStarsameGoods> list = phStarsameGoodsRepository.findByStarsameIdOrderByCreateTimeDesc(phStarsame.getId());
            PhUserInfo userInfo = userInfoService.findByUnionid(unionid);
            String starsamePraise = stringRedisTemplate.opsForValue().get(STARSAME_PRAISE + "_" + phStarsame.getId() + "_" + userInfo.getId());
            Map<String, Object> map1 = objectMapper.convertValue(phStarsame, Map.class);
            if (StringUtils.isBlank(starsamePraise)) {
                map1.put("praise", "0");
            } else {
                map1.put("praise", "1");
            }
            List<PhStarsameGoods> goods = new ArrayList<>();
            List<PhStarsameGoods> brands = new ArrayList<>();

            for (PhStarsameGoods phStarsameGoods : list) {
                if ("1".equals(phStarsameGoods.getType())) {
                    brands.add(phStarsameGoods);
                } else {
                    goods.add(phStarsameGoods);
                }
            }
            map1.put("goods", goods);
            map1.put("brands", brands);
            map1.put("banner", phStarsameImageRepository.findImageByStarsameIdOrderBySortAsc(phStarsame.getId()));
            objectList.add(map1);
        }
        map.put("celebrity", celebrityList);
        map.put("detail", objectList);
        return jsonResultHelper.buildSuccessJsonResult(map);
    }

    @ApiOperation("明星同款列表")
    @GetMapping("/listmini")
    public JsonResult listmini(
            @ApiParam("页码,从0开始") @RequestParam int page,
            @ApiParam("页大小") @RequestParam int size,
            @ApiParam("明星姓名") @RequestParam(required = false) String starName,
            @ApiParam("用户ID") @RequestParam(required = false) Long userid,
            @ApiParam("排序字段[sort-APP,sortMini-小程序]") @RequestParam(defaultValue = "sortMini") String sortName) {
        Page<PhStarsame> phStarsames = phStarsameService.findByPage(starName, PageRequest.of(page, size, Sort.by(Sort.Order.asc(sortName), Sort.Order.desc("createTime"))),sortName);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>();
        List<Object> objectList = new ArrayList<>();
        for (PhStarsame phStarsame : phStarsames) {
            List<PhStarsameGoods> list = phStarsameGoodsRepository.findByStarsameIdOrderByCreateTimeDesc(phStarsame.getId());
            if (null != userid) {
                String starsamePraise = stringRedisTemplate.opsForValue().get(STARSAME_PRAISE + "_" + phStarsame.getId() + "_" + userid);
                Map<String, Object> map1 = objectMapper.convertValue(phStarsame, Map.class);
                if (StringUtils.isBlank(starsamePraise)) {
                    map1.put("praise", "0");
                } else {
                    map1.put("praise", "1");
                }
                List<PhStarsameGoods> goods = new ArrayList<>();
                List<PhStarsameGoods> brands = new ArrayList<>();

                for (PhStarsameGoods phStarsameGoods : list) {
                    if ("1".equals(phStarsameGoods.getType())) {
                        brands.add(phStarsameGoods);
                    } else {
                        goods.add(phStarsameGoods);
                    }
                }
                map1.put("goods", goods);
                map1.put("brands", brands);
                map1.put("banner", phStarsameImageRepository.findImageByStarsameIdOrderBySortAsc(phStarsame.getId()));
                //map1.put("goods",list);
                List<PhCelebrityList> celebrityLists =  phCelebrityListService.findByName(phStarsame.getStarName());
                List<PhCelebrityList> celebrityLists1 = new ArrayList<>();
                for (PhCelebrityList celebrityList : celebrityLists) {
                    celebrityList.setFansSum(celebrityList.getFansSum()+celebrityList.getFans());
                    celebrityLists1.add(celebrityList);
                }
                map1.put("celebrity", celebrityLists1);
                objectList.add(map1);
            } else {
                Map<String, Object> map1 = objectMapper.convertValue(phStarsame, Map.class);

                List<PhStarsameGoods> goods = new ArrayList<>();
                List<PhStarsameGoods> brands = new ArrayList<>();

                for (PhStarsameGoods phStarsameGoods : list) {
                    if ("1".equals(phStarsameGoods.getType())) {
                        brands.add(phStarsameGoods);
                    } else {
                        goods.add(phStarsameGoods);
                    }
                }
                map1.put("goods", goods);
                map1.put("brands", brands);
                map1.put("banner", phStarsameImageRepository.findImageByStarsameIdOrderBySortAsc(phStarsame.getId()));
//				map1.put("goods",list);
                List<PhCelebrityList> celebrityLists =  phCelebrityListService.findByName(phStarsame.getStarName());
                List<PhCelebrityList> celebrityLists1 = new ArrayList<>();
                for (PhCelebrityList celebrityList : celebrityLists) {
                    celebrityList.setFansSum(celebrityList.getFansSum()+celebrityList.getFans());
                    celebrityLists1.add(celebrityList);
                }
                map1.put("celebrity", celebrityLists1);
                objectList.add(map1);
            }
        }
        map.put("content", objectList);
        return jsonResultHelper.buildSuccessJsonResult(map);
    }
}
