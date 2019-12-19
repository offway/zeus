package cn.offway.zeus.controller;

import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.service.JPushService;
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
    @Autowired
    private JPushService jPushService;
    private StringRedisTemplate stringRedisTemplate;
    private static final String KEY_REWARD_LIST = "DoubleZero_REWARD_LIST_{0}";
    private static final String KEY_REWARD_STOCK_TODAY = "DoubleZero_REWARD_STOCK_{0}";
    private static final String KEY_REWARD_STATUS_TODAY = "DoubleZero_REWARD_STATUS_{0}";
    private static final String KEY_REWARD_NOTIFY = "DoubleZero_REWARD_NOTIFY_{0}";
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
        refreshDateTime();
        return format.format(now.toDate());
    }

    @GetMapping("/showTimeStamp")
    public Long showTimeStamp() {
        refreshDateTime();
        return now.getMillis();
    }

    private String getRewardListKey(String userId) {
        return MessageFormat.format(KEY_REWARD_LIST, userId);
    }

    private String getStockKey() {
        return MessageFormat.format(KEY_REWARD_STOCK_TODAY, todayStr);
    }

    private String getStockKey(String dateStr) {
        return MessageFormat.format(KEY_REWARD_STOCK_TODAY, dateStr);
    }

    private String getStatusKey() {
        return MessageFormat.format(KEY_REWARD_STATUS_TODAY, todayStr);
    }

    private String getStatusKey(String dateStr) {
        return MessageFormat.format(KEY_REWARD_STATUS_TODAY, dateStr);
    }

    private String getNotifyKey(String userId) {
        return MessageFormat.format(KEY_REWARD_NOTIFY, userId);
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
        setRedisTemplate();
        refreshDateTime();
        if (!checkUser(userId)) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
        }
        String notifyKey = getNotifyKey(userId);
        List<Object> list = new ArrayList<>();
        for (String dataStr : dateStrArr) {
            Map<String, Object> item = new HashMap<>();
            initRewardStockDataIfNeed(dataStr);
            initGotStatusDataIfNeed(userId, dataStr);
            DateTime timePoint = new DateTime();
            String[] YMD = dataStr.split("-");
            item.put("date", MessageFormat.format("{0}月{1}日", YMD[1], YMD[2]));
            item.put("dateStd", dataStr);
            //订阅通知情况
            if (stringRedisTemplate.opsForHash().hasKey(notifyKey, dataStr)) {
                item.put("subscribe", 1);
            } else {
                item.put("subscribe", 0);
            }
            timePoint = timePoint.withDate(Integer.valueOf(YMD[0]), Integer.valueOf(YMD[1]), Integer.valueOf(YMD[2]));
            DateTime startPoint = timePoint.withTime(12, 0, 0, 0);
            DateTime stopPoint = timePoint.withTime(23, 59, 59, 0);
            item.put("dateTimeStart", startPoint.getMillis());
            item.put("dateTimeStop", stopPoint.getMillis());
            if (now.getMillis() >= startPoint.getMillis() && stopPoint.getMillis() >= now.getMillis()) {
                item.put("status", 4);//可领取
            } else if (startPoint.getMillis() > now.getMillis()) {
                item.put("status", 0);//尚未开始
            } else if (now.getMillis() > stopPoint.getMillis()) {
                item.put("status", 1);//已经结束
            }
            long statusData = getData(userId, getStatusKey(dataStr));
            //库存列表
            Map<Object, Object> stocks = stringRedisTemplate.opsForHash().entries(getStockKey(dataStr));
            List<Object> rewardList = new ArrayList<>();
            int i = 0;
            for (Object k : stocks.keySet()) {
                Map<String, Object> reward = new HashMap<>();
                reward.put("id", i);
                String key = String.valueOf(k);
                reward.put("name", key);
                if (now.getMillis() >= startPoint.getMillis() && stopPoint.getMillis() >= now.getMillis()) {
                    //库存数量
                    int v = Integer.valueOf(String.valueOf(stocks.get(k)));
                    //领取状态
                    int got = (statusData & 1 << i) == 0 ? 0 : 1;
                    if (v <= 0) {
                        reward.put("status", 2);//已抢光
                    } else if (got > 0) {
                        reward.put("status", 3);//已领取
                    } else {
                        reward.put("status", 4);//可领取
                    }
                } else if (startPoint.getMillis() > now.getMillis()) {
                    reward.put("status", 0);//尚未开始
                } else if (now.getMillis() > stopPoint.getMillis()) {
                    reward.put("status", 1);//已经结束
                }
                i++;
                rewardList.add(reward);
            }
            item.put("reward", rewardList);
            list.add(item);
        }
        return jsonResultHelper.buildSuccessJsonResult(list);
    }

    private int[] count = new int[]{0, 0, 0, 2, 3, 100, 100};
    private String[] rewards = new String[]{"666无门槛代金券", "200无门槛代金券", "满1000-500", "满1000-200", "满500-100", "满300-50", "满100-15"};
    private String[] dateStrArr = new String[]{"2019-12-25", "2019-12-26", "2019-12-27", "2019-12-28", "2019-12-29", "2019-12-30", "2019-12-31", "2020-01-01"};

    private void generateRewardPool(String stockKey) {
        int index = 0;
        for (int i : count) {
            stringRedisTemplate.opsForHash().putIfAbsent(stockKey, rewards[index], i);
            index++;
        }
    }

    private void initRewardStockDataIfNeed(String dateStr) {
        String stockKey = dateStr == null ? getStockKey() : getStockKey(dateStr);
        Boolean redisKeyExist = stringRedisTemplate.hasKey(stockKey);
        if (redisKeyExist == null || !redisKeyExist) {
            //生成奖品池
            generateRewardPool(stockKey);
        }
    }

    private void initGotStatusDataIfNeed(String userId, String dateStr) {
        String statusKey = dateStr == null ? getStatusKey() : getStatusKey(dateStr);
        Boolean redisKeyExist = stringRedisTemplate.hasKey(statusKey);
        if (redisKeyExist == null || !redisKeyExist) {
            stringRedisTemplate.opsForHash().putIfAbsent(statusKey, userId, 0);
        }
    }

    @ApiOperation("订阅通知")
    @PostMapping("/subscribe")
    public JsonResult subscribe(
            @ApiParam("用户ID") @RequestParam String userId,
            @ApiParam("日期") @RequestParam String dateStd) {
        setRedisTemplate();
        refreshDateTime();
        if (!checkUser(userId)) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
        }
        String key = getNotifyKey(userId);
        if (stringRedisTemplate.opsForHash().hasKey(key, dateStd)) {
            //重复请求，忽略
        } else {
            Map<String, String> args = new HashMap<>();
            args.put("type", "");
            args.put("id", "");
            args.put("url", "");
            DateTime timePoint = new DateTime();
            String[] YMD = dateStd.split("-");
            timePoint = timePoint.withDate(Integer.valueOf(YMD[0]), Integer.valueOf(YMD[1]), Integer.valueOf(YMD[2]));
            timePoint = timePoint.withTime(12, 0, 0, 0);
            //添加定时任务到极光
            String scheduleId = jPushService.createSingleSchedule("name", timePoint.toDate(), "title", "alert", args, userId);
            //将极光返回的任务标记写入REDIS
            stringRedisTemplate.opsForHash().putIfAbsent(key, dateStd, scheduleId);
        }
        return jsonResultHelper.buildSuccessJsonResult(null);
    }

    @ApiOperation("取消订阅通知")
    @PostMapping("/unsubscribe")
    public JsonResult unsubscribe(
            @ApiParam("用户ID") @RequestParam String userId,
            @ApiParam("日期") @RequestParam String dateStd) {
        setRedisTemplate();
        refreshDateTime();
        if (!checkUser(userId)) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
        }
        String key = getNotifyKey(userId);
        Object scheduleId = stringRedisTemplate.opsForHash().get(key, dateStd);
        if (scheduleId != null) {
            //从极光删除任务
            jPushService.deleteSchedule(String.valueOf(scheduleId));
        }
        //从REDIS删除标记
        stringRedisTemplate.opsForHash().delete(key, dateStd);
        return jsonResultHelper.buildSuccessJsonResult(null);
    }

    @ApiOperation("领取")
    @PostMapping("/get")
    public JsonResult get(
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
        initRewardStockDataIfNeed(null);
        initGotStatusDataIfNeed(userId, null);
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
                voucherInfoService.giveVoucher(userIdLong, 143L);
                finalRewardStr = "满1000-200优惠券";
                stringRedisTemplate.opsForList().leftPush(redisKey, finalRewardStr);
                break;
            case "满500-100":
                voucherInfoService.giveVoucher(userIdLong, 144L);
                finalRewardStr = "满500-100优惠券";
                stringRedisTemplate.opsForList().leftPush(redisKey, finalRewardStr);
                break;
            case "满300-50":
                voucherInfoService.giveVoucher(userIdLong, 145L);
                finalRewardStr = "满300-50优惠券";
                stringRedisTemplate.opsForList().leftPush(redisKey, finalRewardStr);
                break;
            case "满100-15":
                voucherInfoService.giveVoucher(userIdLong, 146L);
                finalRewardStr = "满100-15优惠券";
                stringRedisTemplate.opsForList().leftPush(redisKey, finalRewardStr);
                break;
            default:
                break;
        }
        return jsonResultHelper.buildSuccessJsonResult(finalRewardStr);
    }
}
