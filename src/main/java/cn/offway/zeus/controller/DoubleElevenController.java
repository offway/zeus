package cn.offway.zeus.controller;

import cn.offway.zeus.service.PhVoucherInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

@Api(tags = {"双11活动"})
@RestController
@RequestMapping("/DoubleEleven")
public class DoubleElevenController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JsonResultHelper jsonResultHelper;
    @Autowired
    private PhVoucherInfoService voucherInfoService;
    private StringRedisTemplate stringRedisTemplate;
    private static final String KEY_REWARD_LIST = "DoubleEleven_REWARD_LIST_{0}";
    private static final String KEY_LOTTERY = "DoubleEleven_LOTTERY";
    private static final String KEY_SHARE = "DoubleEleven_SHARE";
    private static final String KEY_SPECIAL_REWARD_1 = "DoubleEleven_SPECIAL_REWARD_1";
    private static final String KEY_SPECIAL_REWARD_2 = "DoubleEleven_SPECIAL_REWARD_2";
    private String todayStr;
    private DateTime now;
    private SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public DoubleElevenController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private void setRedisTemplate() {
        this.stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        this.stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
        this.stringRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
        this.stringRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
    }

    private long getData(String userId, String key) {
        long data = 0L;
        if (stringRedisTemplate.opsForHash().hasKey(key, userId)) {
            data = Long.valueOf(String.valueOf(stringRedisTemplate.opsForHash().get(key, userId)));
        }
        return data;
    }

    @GetMapping("/showTime")
    public String showTime() {
        return format.format(now.toDate());
    }

    private String getRewardListKey(String userId) {
        return MessageFormat.format(KEY_REWARD_LIST, userId);
    }

    private void refreshDateTime() {
        todayStr = formatYMD.format(new Date());
        now = new DateTime();
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

    @ApiOperation("分享获取抽奖次数")
    @PostMapping("/share")
    public JsonResult share(
            @ApiParam("用户ID") @RequestParam String userId) {
        setRedisTemplate();
        refreshDateTime();
        String dayKey = MessageFormat.format("{0}_{1}", userId, todayStr);
        long shareData = getShareTimes(userId);
        if (shareData > 0L) {
            //具体发奖逻辑代码
            stringRedisTemplate.opsForHash().putIfAbsent(KEY_LOTTERY, userId, 0);//初始化
            stringRedisTemplate.opsForHash().increment(KEY_LOTTERY, userId, 1L);
            stringRedisTemplate.opsForHash().increment(KEY_SHARE, dayKey, -1L);
//            String redisKey = getRewardListKey(userId);
//            stringRedisTemplate.opsForList().leftPush(redisKey, MessageFormat.format("抽奖券{0}张", 1L));
            return jsonResultHelper.buildSuccessJsonResult(null);
        } else {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.CALL_LIMIT);
        }
    }

    @ApiOperation("活动入口")
    @PostMapping("/index")
    public JsonResult index(
            @ApiParam("用户ID") @RequestParam String userId) {
        setRedisTemplate();
        refreshDateTime();
        long lotteryData = getData(userId, KEY_LOTTERY);
        String redisKey = getRewardListKey(userId);
        Boolean redisKeyExist = stringRedisTemplate.hasKey(redisKey);
        List<String> rewardResult;
        if (redisKeyExist == null || !redisKeyExist) {
            rewardResult = new ArrayList<>();
        } else {
            rewardResult = stringRedisTemplate.opsForList().range(redisKey, 0, 100);
        }
        Map<String, Object> data = new HashMap<>();
        //今日可通过分享额外获取抽奖券次数/数量
        long shareData = getShareTimes(userId);
        //返回数据包装
        data.put("rewardResult", rewardResult);
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

    private List<Map<String, String>> generateRewardPool() {
        double totalProb = 0;
        List<Map<String, String>> rewardPool = new ArrayList<>();
        //5元无门槛代金券
        Map<String, String> rewardObj1 = new HashMap<>();
        rewardObj1.put("prob", "0.3");
        totalProb += 0.3;
        rewardObj1.put("reward", "5元无门槛代金券");
        rewardPool.add(rewardObj1);
        //5-200元现金礼包 除了 满100-5
        Double[] probs = new Double[]{0.06, 0.06, 0.06, 0.06, 0.06};
        String[] rewards = new String[]{"满300-15", "满500-30", "满1000-60", "满1500-100", "满2000-140"};
        int index = 0;
        for (double i : probs) {
            totalProb += i;
            rewardPool.add(buildRewardObj(String.valueOf(i), rewards[index]));
            index++;
        }
        //OFFWAY限量PVC袋子 暂定20个
        stringRedisTemplate.opsForValue().setIfAbsent(KEY_SPECIAL_REWARD_1, "20");
        String tmp1 = stringRedisTemplate.opsForValue().get(KEY_SPECIAL_REWARD_1);
        if (tmp1 != null && Integer.valueOf(tmp1) > 0) {
            Map<String, String> rewardObj3 = new HashMap<>();
            rewardObj3.put("prob", "0.02");
            totalProb += 0.02;
            rewardObj3.put("reward", "OFFWAY限量PVC袋子");
            rewardPool.add(rewardObj3);
        }
        //OFFWAY潮流福袋 暂定5份
        stringRedisTemplate.opsForValue().setIfAbsent(KEY_SPECIAL_REWARD_2, "5");
        String tmp2 = stringRedisTemplate.opsForValue().get(KEY_SPECIAL_REWARD_2);
        if (tmp2 != null && Integer.valueOf(tmp2) > 0) {
            Map<String, String> rewardObj4 = new HashMap<>();
            rewardObj4.put("prob", "0.02");
            totalProb += 0.02;
            rewardObj4.put("reward", "OFFWAY潮流福袋");
            rewardPool.add(rewardObj4);
        }
        //5-200元现金礼包 满100-5
        Map<String, String> rewardObj5 = new HashMap<>();
        rewardObj5.put("prob", String.valueOf(1 - totalProb));//计算余下概率
        rewardObj5.put("reward", "满100-5");
        rewardPool.add(rewardObj5);
        return rewardPool;
    }

    @ApiOperation("抽奖")
    @PostMapping("/lottery")
    public JsonResult lottery(
            @ApiParam("用户ID") @RequestParam String userId) {
        setRedisTemplate();
        refreshDateTime();
        //检查抽奖券库存
        long lotteryData = getData(userId, KEY_LOTTERY);
        if (lotteryData <= 0) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.LOTTERYNUM_LESS);
        }
        //扣除抽奖券库存
        stringRedisTemplate.opsForHash().increment(KEY_LOTTERY, userId, -1L);
        //生成奖品池
        List<Map<String, String>> rewardPool = generateRewardPool();
        //落点随机法获得对应奖品
        String reward = randomPick(rewardPool);
        logger.info("reward is :" + reward);
        //发放奖励
        long userIdLong = Long.valueOf(userId);
        String redisKey = getRewardListKey(userId);
        String finalRewardStr = "无";
        switch (reward) {
            case "5元无门槛代金券":
                voucherInfoService.giveVoucher(userIdLong, 110L);
                stringRedisTemplate.opsForList().leftPush(redisKey, reward);
                finalRewardStr = reward;
                break;
            case "OFFWAY限量PVC袋子":
            case "OFFWAY潮流福袋":
                stringRedisTemplate.opsForList().leftPush(redisKey, reward);
                finalRewardStr = reward;
                break;
            case "满100-5":
                voucherInfoService.giveVoucher(userIdLong, 112L);
                finalRewardStr = "5元优惠券";
                stringRedisTemplate.opsForList().leftPush(redisKey, finalRewardStr);
                break;
            case "满300-15":
                voucherInfoService.giveVoucher(userIdLong, 113L);
                finalRewardStr = "15元优惠券";
                stringRedisTemplate.opsForList().leftPush(redisKey, finalRewardStr);
                break;
            case "满500-30":
                voucherInfoService.giveVoucher(userIdLong, 114L);
                finalRewardStr = "30元优惠券";
                stringRedisTemplate.opsForList().leftPush(redisKey, finalRewardStr);
                break;
            case "满1000-60":
                voucherInfoService.giveVoucher(userIdLong, 115L);
                finalRewardStr = "60元优惠券";
                stringRedisTemplate.opsForList().leftPush(redisKey, finalRewardStr);
                break;
            case "满1500-100":
                voucherInfoService.giveVoucher(userIdLong, 116L);
                finalRewardStr = "100元优惠券";
                stringRedisTemplate.opsForList().leftPush(redisKey, finalRewardStr);
                break;
            case "满2000-140":
                voucherInfoService.giveVoucher(userIdLong, 117L);
                finalRewardStr = "140元优惠券";
                stringRedisTemplate.opsForList().leftPush(redisKey, finalRewardStr);
                break;
            default:
                break;
        }
        //扣除限量奖品库存
        if ("OFFWAY限量PVC袋子".equals(reward)) {
            stringRedisTemplate.opsForValue().decrement(KEY_SPECIAL_REWARD_1);
        } else if ("OFFWAY潮流福袋".equals(reward)) {
            stringRedisTemplate.opsForValue().decrement(KEY_SPECIAL_REWARD_2);
        }
        return jsonResultHelper.buildSuccessJsonResult(finalRewardStr);
    }
}
