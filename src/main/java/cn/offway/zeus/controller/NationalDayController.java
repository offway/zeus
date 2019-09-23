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

@Api(tags = {"国庆活动"})
@RestController
@RequestMapping("/nationalDay")
public class NationalDayController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JsonResultHelper jsonResultHelper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    Date startDate = null;
    private static final String KEY_SIGN = "nationalDay_SIGN";
    private static final String KEY_REWARD = "nationalDay_REWARD";

    public NationalDayController() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        startDate = format.parse("2019-10-01 00:00:00");
    }

    @ApiOperation("签到")
    @PostMapping("/sign")
    public JsonResult sign(
            @ApiParam("用户ID") @RequestParam String userId) {
        DateTime now = new DateTime();
        if (now.getMonthOfYear() != 10) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_END);
        }
        if (now.getDayOfMonth() > 7) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.ACTIVITY_END);
        }
        long data = 0L;
        if (stringRedisTemplate.opsForHash().hasKey(KEY_SIGN, userId)) {
            data = Long.valueOf(String.valueOf(stringRedisTemplate.opsForHash().get(KEY_SIGN, userId)));
        }
        //是否签过到
        if ((data & 1L << (now.getDayOfMonth() - 1)) != 0) {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.SIGNED);
        }
        //签到
        data = data | 1L << (now.getDayOfMonth() - 1);
        stringRedisTemplate.opsForHash().put(KEY_SIGN, userId, data);
        return jsonResultHelper.buildSuccessJsonResult(null);
    }
}
