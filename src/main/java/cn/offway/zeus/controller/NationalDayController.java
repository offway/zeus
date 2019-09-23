package cn.offway.zeus.controller;

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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Api(tags = {"国庆活动"})
@RestController
@RequestMapping("/nationalDay")
public class NationalDayController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JsonResultHelper jsonResultHelper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final String KEY_SIGN = "nationalDay_SIGN";
    private static final String KEY_REWARD = "nationalDay_REWARD";
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
        //TODO
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
        Map<String, LinkedList<Object>> data = new HashMap<>();
        LinkedList<Object> signList = new LinkedList<>();
        LinkedList<Object> rewardList = new LinkedList<>();
        Map<String, String> defaultSignMap = new HashMap<>();
        Map<String, String> defaultRewardMap = new HashMap<>();
        for (int i = 0; i < 7; i++) {
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
        return jsonResultHelper.buildSuccessJsonResult(data);
    }
}
