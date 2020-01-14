package cn.offway.zeus.controller;

import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.service.PhVoucherInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags = {"春节活动"})
@RestController
@RequestMapping("/NewSpring")
public class NewSpringController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JsonResultHelper jsonResultHelper;
    @Autowired
    private PhVoucherInfoService voucherInfoService;
    @Autowired
    private PhUserInfoService userInfoService;
    private StringRedisTemplate stringRedisTemplate;
    private static final String KEY_REWARD_LIST = "NewSpring_REWARD_LIST_{0}";
    private static final String KEY_LOTTERY = "NewSpring_LOTTERY";
    private static final String KEY_SHARE = "NewSpring_SHARE";
    private static final String KEY_CHAR = "NewSpring_CHAR_{0}";
    private static final String KEY_SPECIAL_REWARD_1 = "NewSpring_SPECIAL_REWARD_1";
    private String todayStr;
    private DateTime now;
    private SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public NewSpringController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private void setRedisTemplate() {
        this.stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        this.stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
        this.stringRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
        this.stringRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
    }

    private long getData(String hashKey, String key) {
        long data = 0L;
        if (stringRedisTemplate.opsForHash().hasKey(key, hashKey)) {
            data = Long.valueOf(String.valueOf(stringRedisTemplate.opsForHash().get(key, hashKey)));
        }
        return data;
    }

    @GetMapping("/showTime")
    public String showTime() {
        refreshDateTime();
        return format.format(now.toDate());
    }

    private String getRewardListKey(String userId) {
        return MessageFormat.format(KEY_REWARD_LIST, userId);
    }

    private String getCharKey(String userId) {
        return MessageFormat.format(KEY_CHAR, userId);
    }

    private void refreshDateTime() {
        todayStr = formatYMD.format(new Date());
        now = new DateTime();
    }

    private boolean checkUser(String uid) {
        if (StringUtils.isNotBlank(uid)) {
            PhUserInfo userInfo = userInfoService.findById(Long.valueOf(uid));
            if (userInfo != null) {
                return StringUtils.isNotBlank(userInfo.getPhone());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private long getShareTimes(String userId) {
        String dayKey = MessageFormat.format("{0}_{1}", userId, todayStr);
        Boolean isExist = stringRedisTemplate.opsForHash().hasKey(KEY_SHARE, dayKey);
        long shareData;
        if (isExist != null && isExist) {
            shareData = getData(dayKey, KEY_SHARE);
        } else {
            shareData = 3L;
            stringRedisTemplate.opsForHash().putIfAbsent(KEY_SHARE, dayKey, shareData);
        }
        return shareData;
    }

    private long getLotteryTimes(String userId) {
        String dayKey = MessageFormat.format("{0}_{1}", userId, todayStr);
        stringRedisTemplate.opsForHash().putIfAbsent(KEY_LOTTERY, dayKey, 2);
        return getData(dayKey, KEY_LOTTERY);
    }

    @ApiOperation("分享获取抽奖次数")
    @PostMapping("/share")
    public JsonResult share(
            @ApiParam("用户ID") @RequestParam String userId) {
        setRedisTemplate();
        refreshDateTime();
        if (!checkUser(userId)) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
        }
        String dayKey = MessageFormat.format("{0}_{1}", userId, todayStr);
        long shareData = getShareTimes(userId);
        if (shareData > 0L) {
            //具体发奖逻辑代码
            stringRedisTemplate.opsForHash().putIfAbsent(KEY_LOTTERY, dayKey, 2);//初始化
            stringRedisTemplate.opsForHash().increment(KEY_LOTTERY, dayKey, 1L);
            stringRedisTemplate.opsForHash().increment(KEY_SHARE, dayKey, -1L);
//            String redisKey = getRewardListKey(userId);
//            stringRedisTemplate.opsForList().leftPush(redisKey, MessageFormat.format("抽奖券{0}张", 1L));
            return jsonResultHelper.buildSuccessJsonResult(null);
        } else {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.CALL_LIMIT);
        }
    }

    @ApiOperation("新用户分享获取")
    @PostMapping("/shareNewUser")
    public JsonResult shareNewUser(
            @ApiParam("用户ID") @RequestParam String userId) {
        setRedisTemplate();
        refreshDateTime();
        if (!checkUser(userId)) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
        }
        //生成奖品池
        List<Map<String, String>> rewardPool = generateRewardPoolNewUser();
        //落点随机法获得对应奖品
        String reward = randomPick(rewardPool);
        logger.info("新用户分享获取 reward is :" + reward);
        String redisKey = getCharKey(userId);
        stringRedisTemplate.opsForHash().increment(redisKey, reward, 1);
        return jsonResultHelper.buildSuccessJsonResult(null);
    }

    @ApiOperation("活动入口")
    @PostMapping("/index")
    public JsonResult index(
            @ApiParam("用户ID") @RequestParam String userId) {
        setRedisTemplate();
        refreshDateTime();
        if (!checkUser(userId)) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
        }
        //抽奖机会
        long lotteryData = getLotteryTimes(userId);
        //今日可通过分享额外获取抽奖机会次数/数量
        long shareData = getShareTimes(userId);
        //奖品列表
        String redisKey = getRewardListKey(userId);
        Boolean redisKeyExist = stringRedisTemplate.hasKey(redisKey);
        List<String> rewardResult;
        if (redisKeyExist == null || !redisKeyExist) {
            rewardResult = new ArrayList<>();
        } else {
            rewardResult = stringRedisTemplate.opsForList().range(redisKey, 0, 100);
        }
        //字列表
        String word = "20二零新春快乐";
        String charKey = getCharKey(userId);
        for (String s : word.split("")) {
            stringRedisTemplate.opsForHash().putIfAbsent(charKey, s, 0);
        }
        Map<Object, Object> chars = stringRedisTemplate.opsForHash().entries(charKey);
        //返回数据包装
        Map<String, Object> data = new HashMap<>();
        data.put("rewardResult", rewardResult);
        data.put("chars", chars);
        data.put("lotteryData", lotteryData);
        data.put("shareTimes", shareData);
        return jsonResultHelper.buildSuccessJsonResult(data);
    }

    private String randomPick(List<Map<String, String>> rewardPool) {
        double randomRate = Math.random();
        double p = 0;
        for (Map<String, String> m : rewardPool) {
            p += Double.valueOf(m.get("prob"));
            if (randomRate <= p) {
                return m.get("reward");
            }
        }
        return "";
    }

    private Map<String, String> buildRewardObj(String prob, String name) {
        Map<String, String> rewardObj = new HashMap<>();
        rewardObj.put("prob", prob);
        rewardObj.put("reward", name);
        return rewardObj;
    }

    private List<Map<String, String>> generateRewardPoolNewUser() {
        List<Map<String, String>> rewardPool = new ArrayList<>();
        //新人免费获取一个字的概率
        Double[] probs = new Double[]{0.2, 0.1, 0.2, 0.2, 0.1, 0.2};
        String[] rewards = new String[]{"2", "0", "零", "新", "春", "快"};//20二零新春快乐
        int index = 0;
        for (double i : probs) {
            rewardPool.add(buildRewardObj(String.valueOf(i), rewards[index]));
            index++;
        }
        return rewardPool;
    }

    private List<Map<String, String>> generateRewardPool() {
        double totalProb = 0;
        List<Map<String, String>> rewardPool = new ArrayList<>();
        //20新春快
        Double[] probs = new Double[]{0.25, 0.1, 0.235, 0.075, 0.15};
        String[] rewards = new String[]{"2", "0", "新", "春", "快"};//20二零新春快乐
        int index = 0;
        for (double i : probs) {
            totalProb += i;
            rewardPool.add(buildRewardObj(String.valueOf(i), rewards[index]));
            index++;
        }
        //乐（5%50张）
        stringRedisTemplate.opsForValue().setIfAbsent(KEY_SPECIAL_REWARD_1, "50");
        String tmp1 = stringRedisTemplate.opsForValue().get(KEY_SPECIAL_REWARD_1);
        if (tmp1 != null && Integer.valueOf(tmp1) > 0) {
            Map<String, String> rewardObj3 = new HashMap<>();
            rewardObj3.put("prob", "0.05");
            totalProb += 0.05;
            rewardObj3.put("reward", "乐");
            rewardPool.add(rewardObj3);
        }
        //零（15% 20%）
        Map<String, String> rewardObj5 = new HashMap<>();
        rewardObj5.put("prob", String.valueOf(1 - totalProb));//计算余下概率
        rewardObj5.put("reward", "零");
        rewardPool.add(rewardObj5);
        return rewardPool;
    }

    @ApiOperation("抽奖")
    @PostMapping("/lottery")
    public JsonResult lottery(
            @ApiParam("用户ID") @RequestParam String userId) {
        setRedisTemplate();
        refreshDateTime();
        if (!checkUser(userId)) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
        }
        //检查抽奖券库存
        long lotteryData = getLotteryTimes(userId);
        if (lotteryData <= 0) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.LOTTERYNUM_LESS);
        }
        //扣除抽奖券库存
        String dayKey = MessageFormat.format("{0}_{1}", userId, todayStr);
        stringRedisTemplate.opsForHash().increment(KEY_LOTTERY, dayKey, -1L);
        //生成奖品池
        List<Map<String, String>> rewardPool = generateRewardPool();
        //落点随机法获得对应奖品
        String reward = randomPick(rewardPool);
        logger.info("reward is :" + reward);
        //发放奖励(字)
        String charKey = getCharKey(userId);
        stringRedisTemplate.opsForHash().increment(charKey, reward, 1);
        //扣除限量奖品库存
        if ("乐".equals(reward)) {
            stringRedisTemplate.opsForValue().decrement(KEY_SPECIAL_REWARD_1);
        }
        return jsonResultHelper.buildSuccessJsonResult(reward);
    }

    @ApiOperation("领取")
    @PostMapping("/getprize")
    public JsonResult getPrize(
            @ApiParam("用户ID") @RequestParam String userId,
            @ApiParam("奖品索引[0-新春快乐,1-20,2-新春,3-0,4-春,5-2020,6-20快乐,7-2020新春快乐]") @RequestParam int rewardIndex) {
        //20二零新春快乐
        String redisKey = getCharKey(userId);
        Map<Object, Object> chars = stringRedisTemplate.opsForHash().entries(redisKey);
        Long twoSum = 0L;
        if (chars.get("2")!=null){
            twoSum = Long.valueOf(chars.get("2").toString());
        }
        Long zeroSum = 0L;
        if (chars.get("0")!=null){
            zeroSum = Long.valueOf(chars.get("0").toString());
        }
        Long xinSum = 0L;
        if (chars.get("新")!=null){
            xinSum = Long.valueOf(chars.get("新").toString());
        }
        Long chunSum = 0L;
        if (chars.get("春")!=null){
            chunSum = Long.valueOf(chars.get("春").toString());
        }
        Long kuaiSum = 0L;
        if (chars.get("快")!=null){
            kuaiSum = Long.valueOf(chars.get("快").toString());
        }
        Long leSum = 0L;
        if (chars.get("乐")!=null){
            leSum = Long.valueOf(chars.get("乐").toString());
        }
//        Long xinSum = Long.valueOf(chars.get("新").toString());
//        Long chunSum = Long.valueOf(chars.get("春").toString());
//        Long kuaiSum = Long.valueOf(chars.get("快").toString());
//        Long leSum = Long.valueOf(chars.get("乐").toString());
        long userIdLong = Long.valueOf(userId);
        String redisLogKey = getRewardListKey(userId);
        String finalRewardStr = "集字条件不足，无法兑换";
        switch (rewardIndex){
            case 0:
                //新春快乐(雅漾洗面奶)
                if (xinSum>=1&&chunSum>=1&&kuaiSum>=1&&leSum>=1){
                    //领取
                    finalRewardStr = "新春快乐(雅漾洗面奶)";
                    String[] rewards = new String[]{"新", "春", "快", "乐"};//新春快乐
                    for (String reward : rewards) {
                        stringRedisTemplate.opsForHash().increment(redisKey, reward, -1);
                    }
                    stringRedisTemplate.opsForList().leftPush(redisLogKey, finalRewardStr);
                }
                break;
            case 1:
                //20(899减120元优惠券)
                if (twoSum>=1&&zeroSum>=1){
                    //领取
                    voucherInfoService.giveVoucher(userIdLong, 7L);
                    finalRewardStr = "20(899减120元优惠券)";
                    String[] rewards = new String[]{"2", "0"};//20
                    for (String reward : rewards) {
                        stringRedisTemplate.opsForHash().increment(redisKey, reward, -1);
                    }
                    stringRedisTemplate.opsForList().leftPush(redisLogKey, finalRewardStr);
                }
                break;
            case 2:
                //新春(499减70元优惠券)
                if (xinSum>=1&&chunSum>=1){
                    //领取
                    voucherInfoService.giveVoucher(userIdLong, 8L);
                    finalRewardStr = "新春(499减70元优惠券)";
                    String[] rewards = new String[]{"新", "春"};//新春
                    for (String reward : rewards) {
                        stringRedisTemplate.opsForHash().increment(redisKey, reward, -1);
                    }
                    stringRedisTemplate.opsForList().leftPush(redisLogKey, finalRewardStr);
                }
                break;
            case 3:
                //0(199减20元优惠券)
                if (zeroSum>=1){
                    //领取
                    voucherInfoService.giveVoucher(userIdLong, 9L);
                    finalRewardStr = "0(199减20元优惠券)";
                    stringRedisTemplate.opsForHash().increment(redisKey, "0", -1);
                    stringRedisTemplate.opsForList().leftPush(redisLogKey, finalRewardStr);
                }
                break;
            case 4:
                //春(5元无门槛代金券)
                if (chunSum>=1){
                    //领取
                    voucherInfoService.giveVoucher(userIdLong, 10L);
                    finalRewardStr = "春(5元无门槛代金券)";
                    stringRedisTemplate.opsForHash().increment(redisKey, "春", -1);
                    stringRedisTemplate.opsForList().leftPush(redisLogKey, finalRewardStr);
                }
                break;
            default:
                break;
        }
        return jsonResultHelper.buildSuccessJsonResult(finalRewardStr);
    }
}
