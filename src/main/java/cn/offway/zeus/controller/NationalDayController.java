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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags = {"国庆活动"})
@RestController
@RequestMapping("/nationalDay")
public class NationalDayController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JsonResultHelper jsonResultHelper;
    @Autowired
    private PhVoucherInfoService voucherInfoService;
    private StringRedisTemplate stringRedisTemplate;
    private static final String KEY_SIGN = "nationalDay_SIGN";
    private static final String KEY_REWARD = "nationalDay_REWARD";
    private static final String KEY_LOTTERY = "nationalDay_LOTTERY";
    private static final String KEY_REWARD_LIST = "nationalDay_REWARD_LIST_{0}";
    private static final String KEY_SHARE = "nationalDay_SHARE";
    private static final String KEY_SPECIAL_REWARD_1 = "nationalDay_SPECIAL_REWARD_1";
    private static final String KEY_SPECIAL_REWARD_2 = "nationalDay_SPECIAL_REWARD_2";
    private String todayStr;
    private DateTime now;
    private SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    public NationalDayController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private void setRedisTemplate() {
        this.stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        this.stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
        this.stringRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
        this.stringRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
    }

    private boolean isClose() {
        todayStr = formatYMD.format(new Date());
        now = new DateTime();
        return now.getMonthOfYear() != 10 || now.getDayOfMonth() > 7;
    }

    private long getData(String userId, String key) {
        long data = 0L;
        if (stringRedisTemplate.opsForHash().hasKey(key, userId)) {
            data = Long.valueOf(String.valueOf(stringRedisTemplate.opsForHash().get(key, userId)));
        }
        return data;
    }

    private boolean isSignedOrIsGot(long data) {
        return (data & 1L << (now.getDayOfMonth() - 1)) != 0;
    }

    private boolean isSignedOrIsGot(long data, int theDay) {
        return (data & 1L << theDay) != 0;
    }

    @ApiOperation("签到(过期)")
    @PostMapping("/sign_deprecated")
    @Deprecated
    public JsonResult sign(
            @ApiParam("用户ID") @RequestParam String userId) {
        if (isClose()) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_END);
        }
        long data = getData(userId, KEY_SIGN);
        //是否签过到
        if (isSignedOrIsGot(data)) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.SIGNED);
        }
        //签到
        data = data | 1L << (now.getDayOfMonth() - 1);
        stringRedisTemplate.opsForHash().put(KEY_SIGN, userId, data);
        return jsonResultHelper.buildSuccessJsonResult(null);
    }

    @ApiOperation("领取签到奖励(过期)")
    @PostMapping("/sign_getReward_deprecated")
    @Deprecated
    public JsonResult getSignReward(
            @ApiParam("用户ID") @RequestParam String userId) {
        if (isClose()) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_END);
        }
        long signData = getData(userId, KEY_SIGN);
        //是否签到
        if (!isSignedOrIsGot(signData)) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.NOT_SIGNED);
        }
        long rewardData = getData(userId, KEY_REWARD);
        //是否领过奖励
        if (isSignedOrIsGot(rewardData)) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.VOUCHER_GIVED);
        }
        //领奖
        rewardData = rewardData | 1L << (now.getDayOfMonth() - 1);
        stringRedisTemplate.opsForHash().put(KEY_REWARD, userId, rewardData);
        //具体发奖逻辑代码
        stringRedisTemplate.opsForHash().putIfAbsent(KEY_LOTTERY, userId, 0);//初始化
        switch (now.getDayOfMonth()) {
            case 6:
                stringRedisTemplate.opsForHash().increment(KEY_LOTTERY, userId, 3L);
                break;
            case 7:
                stringRedisTemplate.opsForHash().increment(KEY_LOTTERY, userId, 3L);
                //并送一个优惠券礼包（5元无门槛，99-10，199-20，299-30，399-40，599-60，799-80，999-100）
                String[] voucherProjectIds = {"6", "7", "8", "9", "10", "11"};
                voucherInfoService.giveVoucher(Long.valueOf(userId), Arrays.asList(voucherProjectIds));
                break;
            default:
                stringRedisTemplate.opsForHash().increment(KEY_LOTTERY, userId, 2L);
                break;
        }
        return jsonResultHelper.buildSuccessJsonResult(null);
    }

    private String getRewardListKey(String userId) {
        return MessageFormat.format(KEY_REWARD_LIST, userId);
    }

    @ApiOperation("签到并获得奖励")
    @PostMapping("/sign_doSign")
    public JsonResult signReal(
            @ApiParam("用户ID") @RequestParam String userId) {
        if (isClose()) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_END);
        }
        setRedisTemplate();
        long rewardData = getData(userId, KEY_REWARD);
        //是否领过奖励
        if (isSignedOrIsGot(rewardData)) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.VOUCHER_GIVED);
        }
        //领奖
        rewardData = rewardData | 1L << (now.getDayOfMonth() - 1);
        stringRedisTemplate.opsForHash().putIfAbsent(KEY_LOTTERY, userId, 0);//初始化
        stringRedisTemplate.opsForHash().put(KEY_REWARD, userId, rewardData);
        //具体发奖逻辑代码
        String redisKey = getRewardListKey(userId);
        switch (now.getDayOfMonth()) {
            case 6:
                stringRedisTemplate.opsForHash().increment(KEY_LOTTERY, userId, 3L);
//                stringRedisTemplate.opsForList().leftPush(redisKey, MessageFormat.format("抽奖券{0}张", 3L));
                break;
            case 7:
                stringRedisTemplate.opsForHash().increment(KEY_LOTTERY, userId, 3L);
//                stringRedisTemplate.opsForList().leftPush(redisKey, MessageFormat.format("抽奖券{0}张", 3L));
                //并送一个优惠券礼包（5元无门槛，99-10，199-20，299-30，399-40，599-60，799-80，999-100）
                String[] voucherProjectIds = {"6", "7", "8", "9", "10", "11"};
                String[] voucherProjectNames = {"满100减5", "满300减15", "满500减30", "满1000减60", "满1500减100", "满2000减140"};
                voucherInfoService.giveVoucher(Long.valueOf(userId), Arrays.asList(voucherProjectIds));
                int i = 0;
                for (String s : voucherProjectIds) {
                    stringRedisTemplate.opsForList().leftPush(redisKey, MessageFormat.format("{0}优惠券", voucherProjectNames[i]));
                    i++;
                }
                break;
            default:
                stringRedisTemplate.opsForHash().increment(KEY_LOTTERY, userId, 2L);
//                stringRedisTemplate.opsForList().leftPush(redisKey, MessageFormat.format("抽奖券{0}张", 2L));
                break;
        }
        return jsonResultHelper.buildSuccessJsonResult(null);
    }

    private long getShareTimes(String userId) {
        String dayKey = MessageFormat.format("{0}_{1}", userId, todayStr);
        Boolean isExist = stringRedisTemplate.opsForHash().hasKey(KEY_SHARE, dayKey);
        long shareData;
        if (isExist != null && isExist) {
            shareData = getData(dayKey, KEY_SHARE);
        } else {
            switch (now.getDayOfMonth()) {
                case 6:
                case 7:
                    shareData = 2L;
                    break;
                default:
                    shareData = 1L;
                    break;
            }
            stringRedisTemplate.opsForHash().putIfAbsent(KEY_SHARE, dayKey, shareData);
        }
        return shareData;
    }

    @ApiOperation("分享获取奖励")
    @PostMapping("/share")
    public JsonResult share(
            @ApiParam("用户ID") @RequestParam String userId) {
        if (isClose()) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_END);
        }
        setRedisTemplate();
        String dayKey = MessageFormat.format("{0}_{1}", userId, todayStr);
        long shareData = getShareTimes(userId);
        if (shareData > 0L) {
            //具体发奖逻辑代码
            stringRedisTemplate.opsForHash().putIfAbsent(KEY_LOTTERY, userId, 0);//初始化
//            stringRedisTemplate.opsForHash().putIfAbsent(KEY_SHARE, dayKey, 1);//标记为已使用
            stringRedisTemplate.opsForHash().increment(KEY_LOTTERY, userId, 1L);
            stringRedisTemplate.opsForHash().increment(KEY_SHARE, dayKey, -1L);
//            String redisKey = getRewardListKey(userId);
//            stringRedisTemplate.opsForList().leftPush(redisKey, MessageFormat.format("抽奖券{0}张", 1L));
            return jsonResultHelper.buildSuccessJsonResult(null);
        } else {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.CALL_LIMIT);
        }
    }

    @ApiOperation("签到以及奖励列表(过期)")
    @PostMapping("/sign_reward_list_deprecated")
    @Deprecated
    public JsonResult list(
            @ApiParam("用户ID") @RequestParam String userId) {
        if (isClose()) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_END);
        }
        long signData = getData(userId, KEY_SIGN);
        long rewardData = getData(userId, KEY_REWARD);
        long lotteryData = getData(userId, KEY_LOTTERY);
        Map<String, Object> data = new HashMap<>();
        LinkedList<Object> signList = new LinkedList<>();
        LinkedList<Object> rewardList = new LinkedList<>();
        for (int i = 0; i < 7; i++) {
            Map<String, String> defaultSignMap = new HashMap<>();
            Map<String, String> defaultRewardMap = new HashMap<>();
            if (i > now.getDayOfMonth() - 1) {
                defaultSignMap.put("msg", "尚未开始");
                defaultSignMap.put("code", "-1");
                signList.add(i, defaultSignMap);
                rewardList.add(i, defaultSignMap);
            } else if (i == now.getDayOfMonth() - 1) {
                //签到数据
                if (isSignedOrIsGot(signData, i)) {
                    defaultSignMap.put("msg", "已签到");
                    defaultSignMap.put("code", "1");
                } else {
                    defaultSignMap.put("msg", "点击签到");
                    defaultSignMap.put("code", "0");
                }
                signList.add(i, defaultSignMap);
                //领奖数据
                if (isSignedOrIsGot(rewardData, i)) {
                    defaultRewardMap.put("msg", "已领取");
                    defaultRewardMap.put("code", "1");
                } else {
                    defaultRewardMap.put("msg", "点击领取");
                    defaultRewardMap.put("code", "0");
                }
                rewardList.add(i, defaultRewardMap);
            } else {
                //签到数据
                if (isSignedOrIsGot(signData, i)) {
                    defaultSignMap.put("msg", "已签到");
                    defaultSignMap.put("code", "1");
                } else {
                    defaultSignMap.put("msg", "未签到");
                    defaultSignMap.put("code", "-2");
                }
                signList.add(i, defaultSignMap);
                //领奖数据
                if (isSignedOrIsGot(rewardData, i)) {
                    defaultRewardMap.put("msg", "已领取");
                    defaultRewardMap.put("code", "1");
                } else {
                    defaultRewardMap.put("msg", "未领取");
                    defaultRewardMap.put("code", "-2");
                }
                rewardList.add(i, defaultRewardMap);
            }
        }
        data.put("signList", signList);
        data.put("rewardList", rewardList);
        data.put("lotteryData", lotteryData);
        return jsonResultHelper.buildSuccessJsonResult(data);
    }

    private Map<String, Object> getDefaultData() {
        Map<String, Object> data = new HashMap<>();
        LinkedList<Object> rewardList = new LinkedList<>();
        for (int i = 0; i < 7; i++) {
            Map<String, String> defaultRewardMap = new HashMap<>();
            defaultRewardMap.put("msg", "尚未开始");
            defaultRewardMap.put("code", "-1");
            rewardList.add(i, defaultRewardMap);
        }
        //返回数据包装
        data.put("rewardList", rewardList);
        data.put("rewardResult", new ArrayList<>());
        data.put("lotteryData", 0);
        data.put("shareTimes", 0);
        return data;
    }

    @ApiOperation("签到入口")
    @PostMapping("/sign_index")
    public JsonResult index(
            @ApiParam("用户ID") @RequestParam String userId) {
        if (isClose()) {
            return jsonResultHelper.buildSuccessJsonResult(getDefaultData());
//            return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_END);
        }
        setRedisTemplate();
        long rewardData = getData(userId, KEY_REWARD);
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
        LinkedList<Object> rewardList = new LinkedList<>();
        for (int i = 0; i < 7; i++) {
            Map<String, String> defaultRewardMap = new HashMap<>();
            if (i > now.getDayOfMonth() - 1) {
                defaultRewardMap.put("msg", "尚未开始");
                defaultRewardMap.put("code", "-1");
                rewardList.add(i, defaultRewardMap);
            } else if (i == now.getDayOfMonth() - 1) {
                //领奖数据
                if (isSignedOrIsGot(rewardData, i)) {
                    defaultRewardMap.put("msg", "已领取");
                    defaultRewardMap.put("code", "1");
                } else {
                    defaultRewardMap.put("msg", "点击领取");
                    defaultRewardMap.put("code", "0");
                }
                rewardList.add(i, defaultRewardMap);
            } else {
                //领奖数据
                if (isSignedOrIsGot(rewardData, i)) {
                    defaultRewardMap.put("msg", "已领取");
                    defaultRewardMap.put("code", "1");
                } else {
                    defaultRewardMap.put("msg", "未领取");
                    defaultRewardMap.put("code", "-2");
                }
                rewardList.add(i, defaultRewardMap);
            }
        }
        //今日可通过分享额外获取抽奖券次数/数量
        long shareData = getShareTimes(userId);
        //返回数据包装
        data.put("rewardList", rewardList);
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

    private String randomPick() {
        Double[] probs = new Double[]{0.3, 0.25, 0.15, 0.1, 0.1, 0.1};
        String[] rewards = new String[]{"满100-5", "满300-15", "满500-30", "满1000-60", "满1500-100", "满2000-140"};
        double randomRate = Math.random();
        double p = 0;
        int index = 0;
        for (double i : probs) {
            p += i;
            if (randomRate <= p) {
                return rewards[index];
            }
            index++;
        }
        return "";
    }

    private List<Map<String, String>> generateRewardPool() {
        double totalProb = 0;
        List<Map<String, String>> rewardPool = new ArrayList<>();
        //5元无门槛优惠券
        Map<String, String> rewardObj1 = new HashMap<>();
        rewardObj1.put("prob", "0.2");
        totalProb += 0.2;
        rewardObj1.put("reward", "5元无门槛优惠券");
        rewardPool.add(rewardObj1);
        //10元无门槛优惠券
        Map<String, String> rewardObj2 = new HashMap<>();
        rewardObj2.put("prob", "0.2");
        totalProb += 0.2;
        rewardObj2.put("reward", "10元无门槛优惠券");
        rewardPool.add(rewardObj2);
        //OFFWAY限量项链 暂定5条
        String tmp1 = stringRedisTemplate.opsForValue().get(KEY_SPECIAL_REWARD_1);
        if (tmp1 != null && Integer.valueOf(tmp1) > 0) {
            Map<String, String> rewardObj3 = new HashMap<>();
            rewardObj3.put("prob", "0.02");
            totalProb += 0.02;
            rewardObj3.put("reward", "OFFWAY限量项链");
            rewardPool.add(rewardObj3);
        }
        //OFFWAY福袋 暂定8份
        String tmp2 = stringRedisTemplate.opsForValue().get(KEY_SPECIAL_REWARD_2);
        if (tmp2 != null && Integer.valueOf(tmp2) > 0) {
            Map<String, String> rewardObj4 = new HashMap<>();
            rewardObj4.put("prob", "0.02");
            totalProb += 0.02;
            rewardObj4.put("reward", "OFFWAY福袋");
            rewardPool.add(rewardObj4);
        }
        //5-200元现金礼包
        Map<String, String> rewardObj5 = new HashMap<>();
        rewardObj5.put("prob", String.valueOf(1 - totalProb));//计算余下概率
        rewardObj5.put("reward", "5-200元现金礼包");
        rewardPool.add(rewardObj5);
        return rewardPool;
    }

    @ApiOperation("抽奖")
    @PostMapping("/lottery")
    public JsonResult lottery(
            @ApiParam("用户ID") @RequestParam String userId) {
        if (isClose()) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_END);
        }
        setRedisTemplate();
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
        //是否需要二次抽奖
        if ("5-200元现金礼包".equals(reward)) {
            reward = randomPick();
            logger.info("inner reward is :" + reward);
        }
        //发放奖励
        long userIdLong = Long.valueOf(userId);
        String redisKey = getRewardListKey(userId);
        String finalRewardStr = "无";
        switch (reward) {
            case "5元无门槛优惠券":
                voucherInfoService.giveVoucher(userIdLong, 110L);
                stringRedisTemplate.opsForList().leftPush(redisKey, reward);
                finalRewardStr = reward;
                break;
            case "10元无门槛优惠券":
                voucherInfoService.giveVoucher(userIdLong, 111L);
                stringRedisTemplate.opsForList().leftPush(redisKey, reward);
                finalRewardStr = reward;
                break;
            case "OFFWAY限量项链":
            case "OFFWAY福袋":
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
        if ("OFFWAY限量项链".equals(reward)) {
            stringRedisTemplate.opsForValue().decrement(KEY_SPECIAL_REWARD_1);
        } else if ("OFFWAY福袋".equals(reward)) {
            stringRedisTemplate.opsForValue().decrement(KEY_SPECIAL_REWARD_2);
        }
        return jsonResultHelper.buildSuccessJsonResult(finalRewardStr);
    }
}
