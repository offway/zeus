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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
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
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final String KEY_SIGN = "nationalDay_SIGN";
    private static final String KEY_REWARD = "nationalDay_REWARD";
    private static final String KEY_LOTTERY = "nationalDay_LOTTERY";
    private static final String KEY_SPECIAL_REWARD_1 = "nationalDay_SPECIAL_REWARD_1";
    private static final String KEY_SPECIAL_REWARD_2 = "nationalDay_SPECIAL_REWARD_2";
    private Date startDate;
    private DateTime now;

    public NationalDayController() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        startDate = format.parse("2019-10-01 00:00:00");
        now = new DateTime();
    }

    private boolean isClose() {
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

    @ApiOperation("签到")
    @PostMapping("/sign")
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

    @ApiOperation("领取签到奖励")
    @PostMapping("/sign_getReward")
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

    @ApiOperation("签到以及奖励列表")
    @PostMapping("/sign_reward_list")
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

    private String randomPick(List<Map<String, String>> rewardPool) {
        double randomRate = Math.random();
        double p = 0;
        for (Map<String, String> m : rewardPool) {
            p += Double.valueOf(m.get("prob"));
            if (randomRate <= p) {
                return m.get("reward");
            }
        }
        return null;
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
        return null;
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
        switch (reward) {
            case "5元无门槛优惠券":
                voucherInfoService.giveVoucher(userIdLong, 110L);
                break;
            case "10元无门槛优惠券":
                voucherInfoService.giveVoucher(userIdLong, 111L);
                break;
            case "OFFWAY限量项链":
                //TODO
                break;
            case "OFFWAY福袋":
                //TODO
                break;
            case "满100-5":
                voucherInfoService.giveVoucher(userIdLong, 112L);
                break;
            case "满300-15":
                voucherInfoService.giveVoucher(userIdLong, 113L);
                break;
            case "满500-30":
                voucherInfoService.giveVoucher(userIdLong, 114L);
                break;
            case "满1000-60":
                voucherInfoService.giveVoucher(userIdLong, 115L);
                break;
            case "满1500-100":
                voucherInfoService.giveVoucher(userIdLong, 116L);
                break;
            case "满2000-140":
                voucherInfoService.giveVoucher(userIdLong, 117L);
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
        return jsonResultHelper.buildSuccessJsonResult(null);
    }
}