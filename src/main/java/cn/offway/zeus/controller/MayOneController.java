package cn.offway.zeus.controller;

import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.service.JPushService;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.service.PhVoucherInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import cn.offway.zeus.utils.WXUtil;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags = {"五一活动"})
@RestController
@RequestMapping("/MayOne")
public class MayOneController {
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
    private static final String KEY_REWARD_LIST = "MayOne_REWARD_LIST_{0}";
    private static final String KEY_REWARD_STOCK_TODAY = "MayOne_REWARD_STOCK_{0}";
    private static final String KEY_REWARD_STATUS_TODAY = "MayOne_REWARD_STATUS_{0}";
    private static final String KEY_REWARD_NOTIFY = "MayOne_REWARD_NOTIFY_{0}";
    private static final String KEY_REWARD_NOTIFY_WX = "MayOne_REWARD_NOTIFY_WX_{0}";
    private String todayStr;
    private DateTime now;
    private SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public MayOneController(StringRedisTemplate stringRedisTemplate) {
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

    @Scheduled(cron = "0 55 11 * * *")
    public void pushWX() {
        refreshDateTime();
        if (Arrays.asList(dateStrArr).indexOf(todayStr) >= 0) {
            //推送微信订阅消息
            String keyWX = getNotifyKeyOfWX(todayStr);
            String token = WXUtil.getToken();
            for (Object o : stringRedisTemplate.opsForHash().keys(keyWX)) {
                String openid = String.valueOf(o);
                // 订阅消息包装
                Map<String, Object> args = WXUtil.buildMsg(openid, "url", "五一抢券倒计时5分钟！", "¥1000000无门槛券准点放送，冲！");
                // 推送订阅消息
                WXUtil.sendSubscribeMsg(logger, args, token);
            }
        }
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

    private String getNotifyKeyOfWX(String dateStr) {
        return MessageFormat.format(KEY_REWARD_NOTIFY_WX, dateStr);
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
            //极光订阅通知情况
            if (stringRedisTemplate.opsForHash().hasKey(notifyKey, dataStr)) {
                item.put("subscribe", 1);
            } else {
                item.put("subscribe", 0);
            }
            //微信订阅通知情况
            String notifyKeyWX = getNotifyKeyOfWX(dataStr);
            if (stringRedisTemplate.opsForHash().hasKey(notifyKeyWX, userId)) {
                item.put("subscribe_wx", 1);
            } else {
                item.put("subscribe_wx", 0);
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
    private String[] dateStrArr = new String[]{"2020-04-29", "2020-04-30", "2020-05-01", "2020-05-02", "2020-05-03", "2020-05-04", "2020-05-05"};

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
            @ApiParam("日期") @RequestParam(defaultValue = "2019-12-23") String dateStd,
            @ApiParam("时间") @RequestParam(defaultValue = "11:55:00", required = false) String timeStd) {
        setRedisTemplate();
        refreshDateTime();
        if (!checkUser(userId)) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_NOT_EXISTS);
        }
        //推到微信订阅消息列表
        String keyWX = getNotifyKeyOfWX(dateStd);
        stringRedisTemplate.opsForHash().putIfAbsent(keyWX, userId, "1");
        //推到极光推送
        String key = getNotifyKey(userId);
        if (stringRedisTemplate.opsForHash().hasKey(key, dateStd)) {
            //重复请求，忽略
        } else {
            Map<String, String> args = new HashMap<>();
            args.put("type", "0");//0-H5,1-精选文章,2-活动
            args.put("id", null);
            args.put("url", "url");
            DateTime timePoint = new DateTime();
            String[] YMD = dateStd.split("-");
            String[] HMS = timeStd.split(":");
            timePoint = timePoint.withDate(Integer.valueOf(YMD[0]), Integer.valueOf(YMD[1]), Integer.valueOf(YMD[2]));
            timePoint = timePoint.withTime(Integer.valueOf(HMS[0]), Integer.valueOf(HMS[1]), Integer.valueOf(HMS[2]), 0);
            //添加定时任务到极光
            String scheduleId = jPushService.createSingleSchedule("MayOneSubscribe" + userId, timePoint.toDate(), "五一抢券倒计时5分钟！", "¥1000000无门槛券准点放送，冲！", args, userId);
            if (StringUtils.isBlank(scheduleId)) {
                return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
            }
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
        //从微信订阅消息列表中删除
        String keyWX = getNotifyKeyOfWX(dateStd);
        stringRedisTemplate.opsForHash().delete(keyWX, userId);
        //从极光推送列表删除
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