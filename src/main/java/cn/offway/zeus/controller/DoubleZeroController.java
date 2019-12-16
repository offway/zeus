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

@Api(tags = {"双旦活动"})
@RestController
@RequestMapping("/DoubleZero")
public class DoubleZeroController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JsonResultHelper jsonResultHelper;
    @Autowired
    private PhVoucherInfoService voucherInfoService;
    @Autowired
    private PhUserInfoService userInfoService;
    private StringRedisTemplate stringRedisTemplate;
    private static final String KEY_REWARD_LIST = "DoubleZero_REWARD_LIST_{0}";
    private static final String KEY_REWARD_STOCK_TODAY = "DoubleZero_REWARD_STOCK_{0}";
    private static final String KEY_REWARD_STATUS_TODAY = "DoubleZero_REWARD_STATUS_{0}";
    private String todayStr;
    private DateTime now;
    private SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public DoubleZeroController(StringRedisTemplate stringRedisTemplate) {
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

    private String getStockKey() {
        return MessageFormat.format(KEY_REWARD_STOCK_TODAY, todayStr);
    }

    private String getStatusKey() {
        return MessageFormat.format(KEY_REWARD_STATUS_TODAY, todayStr);
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

    @ApiOperation("活动入口")
    @PostMapping("/index")
    public JsonResult index(
            @ApiParam("用户ID") @RequestParam String userId) {
        Map<String, Object> data = new HashMap<>();
        setRedisTemplate();
        refreshDateTime();
        if (!checkUser(userId)) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
        }
        initRewardStockDataIfNeed();
        initGotStatusDataIfNeed(userId);
        //库存列表
        data.put("stockList", stringRedisTemplate.opsForHash().entries(getStockKey()));
        //领取状态
        Map<String, Integer> status = new HashMap<>();
        long statusData = getData(userId, getStatusKey());
        int i = 0;
        for (String reward : rewards) {
            status.put(reward, (statusData & 1 << i) == 0 ? 0 : 1);
            i++;
        }
        data.put("status", status);
        //奖品列表
        List<String> rewardResult;
        String redisKey = getRewardListKey(userId);
        Boolean redisKeyExist = stringRedisTemplate.hasKey(redisKey);
        if (redisKeyExist == null || !redisKeyExist) {
            rewardResult = new ArrayList<>();
        } else {
            rewardResult = stringRedisTemplate.opsForList().range(redisKey, 0, 100);
        }
        data.put("rewardResult", rewardResult);
        return jsonResultHelper.buildSuccessJsonResult(data);
    }

    private int[] count = new int[]{0, 0, 0, 2, 3, 100, 100};
    private String[] rewards = new String[]{"666无门槛代金券", "200无门槛代金券", "满1000-500", "满1000-200", "满500-100", "满300-50", "满100-15"};

    private void generateRewardPool(String stockKey) {
        int index = 0;
        for (int i : count) {
            stringRedisTemplate.opsForHash().putIfAbsent(stockKey, rewards[index], String.valueOf(i));
            index++;
        }
    }

    private void initRewardStockDataIfNeed() {
        String stockKey = getStockKey();
        Boolean redisKeyExist = stringRedisTemplate.hasKey(stockKey);
        if (redisKeyExist == null || !redisKeyExist) {
            //生成奖品池
            generateRewardPool(stockKey);
        }
    }

    private void initGotStatusDataIfNeed(String userId) {
        String statusKey = getStatusKey();
        Boolean redisKeyExist = stringRedisTemplate.hasKey(statusKey);
        if (redisKeyExist == null || !redisKeyExist) {
            stringRedisTemplate.opsForHash().putIfAbsent(statusKey, userId, 0);
        }
    }

    @ApiOperation("领取")
    @PostMapping("/get")
    public JsonResult lottery(
            @ApiParam("用户ID") @RequestParam String userId,
            @ApiParam("奖品索引") @RequestParam int rewardIndex) {
        setRedisTemplate();
        refreshDateTime();
        if (!checkUser(userId)) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
        }
        if (rewardIndex < 0 || rewardIndex >= rewards.length) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
        }
        initRewardStockDataIfNeed();
        initGotStatusDataIfNeed(userId);
        //检查库存情况
        String reward = rewards[rewardIndex];
        Object stockNum = stringRedisTemplate.opsForHash().get(getStockKey(), reward);
        if (stockNum == null || Integer.valueOf(String.valueOf(stockNum)) <= 0) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.STOCK_SHORTAGE);
        }
        //检查是否领取过
        long statusData = getData(userId, getStatusKey());
        if ((statusData & 1 << rewardIndex) != 0) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_PARTICIPATED);
        }
        //扣除库存
        stringRedisTemplate.opsForHash().increment(getStockKey(), reward, -1L);
        //标记已领取
        long newTag = statusData | 1 << rewardIndex;
        stringRedisTemplate.opsForHash().put(getStatusKey(), userId, newTag);
        //发放奖励
        logger.info("reward is :" + reward);
        long userIdLong = Long.valueOf(userId);
        String redisKey = getRewardListKey(userId);
        String finalRewardStr = "无";
        switch (reward) {
            case "满1000-200":
                voucherInfoService.giveVoucher(userIdLong, 112L);
                finalRewardStr = "满1000-200优惠券";
                stringRedisTemplate.opsForList().leftPush(redisKey, finalRewardStr);
                break;
            case "满500-100":
                voucherInfoService.giveVoucher(userIdLong, 113L);
                finalRewardStr = "满500-100优惠券";
                stringRedisTemplate.opsForList().leftPush(redisKey, finalRewardStr);
                break;
            case "满300-50":
                voucherInfoService.giveVoucher(userIdLong, 114L);
                finalRewardStr = "满300-50优惠券";
                stringRedisTemplate.opsForList().leftPush(redisKey, finalRewardStr);
                break;
            case "满100-15":
                voucherInfoService.giveVoucher(userIdLong, 115L);
                finalRewardStr = "满100-15优惠券";
                stringRedisTemplate.opsForList().leftPush(redisKey, finalRewardStr);
                break;
            default:
                break;
        }
        return jsonResultHelper.buildSuccessJsonResult(finalRewardStr);
    }
}
