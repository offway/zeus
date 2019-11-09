package cn.offway.zeus.controller;

import cn.offway.zeus.domain.PhAccumulatePoints;
import cn.offway.zeus.domain.PhLimitedSale;
import cn.offway.zeus.domain.PhLimitedSaleOp;
import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.service.PhAccumulatePointsService;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;


@Api(tags={"积分"})
@RestController
@RequestMapping("/accumulatePoints")
public class AccumulatePointsController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String READING_KEY = "zeus.points.read";

    private static final String SMS_CODE_KEY="zeus.sms.code";

    @Autowired
    private JsonResultHelper jsonResultHelper;

    @Autowired
    private PhAccumulatePointsService phAccumulatePointsService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PhUserInfoService phUserInfoService;

    @ApiOperation(value = "初始化")
    @GetMapping("/init")
    public JsonResult init(
            @ApiParam(value = "用户ID",required = true) @RequestParam Long userId){
        return jsonResultHelper.buildSuccessJsonResult(phAccumulatePointsService.init(userId));
    }

    @ApiOperation(value = "签到")
    @PostMapping("/sign")
    public JsonResult sign(
            @ApiParam(value = "用户ID",required = true) @RequestParam Long userId){

        int count = phAccumulatePointsService.countByUserIdAndTypeToday(userId,"0");
        if(count > 0){
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.SIGNED);
        }

        try {
            return jsonResultHelper.buildSuccessJsonResult(phAccumulatePointsService.sign(userId));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("积分签到异常",e);
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
        }
    }

    @ApiOperation(value = "积分明细")
    @GetMapping("/list")
    public JsonResult<Page<PhAccumulatePoints>> list(
            @ApiParam(value = "用户ID",required = true) @RequestParam Long userId,
            @ApiParam(value = "页码,从0开始",required = true) @RequestParam int page,
            @ApiParam(value = "页大小",required = true) @RequestParam int size){
        return jsonResultHelper.buildSuccessJsonResult(phAccumulatePointsService.finByPage(userId, PageRequest.of(page, size)));
    }

    @ApiOperation(value = "分享文章")
    @PostMapping("/points")
    public JsonResult points(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "文章ID", required = true) @RequestParam Long articleId) {
        try {

            String type = "2"; //分享文章

            if (phAccumulatePointsService.countByUserIdAndTypeToday(userId, type) > 0) {
                return jsonResultHelper.buildFailJsonResult(CommonResultCode.POINTS_LIMITED);
            }

            phAccumulatePointsService.points(userId, type,20L,"分享了文章："+articleId);
            return jsonResultHelper.buildSuccessJsonResult(null);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("分享文章邀请注册异常：",e);
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
        }
    }

    @ApiOperation("邀请好友注册")
    @PostMapping("/register")
    public JsonResult register(
            @ApiParam(value = "分享用户ID",required = true) @RequestParam Long userId,
            @ApiParam(value = "注册用户手机号",required = true) @RequestParam String phone,
            @ApiParam(value = "注册用户验证码",required = true) @RequestParam String code) throws Exception{


        if(StringUtils.isBlank(phone)){
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_MISS);
        }

        phone = phone.contains("+")?phone:"+86"+phone;

        String smsCode = stringRedisTemplate.opsForValue().get(SMS_CODE_KEY+"_"+phone);
        if(StringUtils.isBlank(smsCode)){
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_INVALID);
        }

        if(!code.equals(smsCode)){
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.SMS_CODE_ERROR);
        }


        PhUserInfo phUserInfo = phUserInfoService.findByPhone(phone);


        if(null != phUserInfo){
           return jsonResultHelper.buildFailJsonResult(CommonResultCode.USER_EXISTS);
        }

        phUserInfo = phUserInfoService.register(phone,null,null,null,null,null,null,null,null);

        String type = "3"; //邀请好友完成注册

        if (phAccumulatePointsService.countByUserIdAndTypeToday(userId, type) < 10) {
            phAccumulatePointsService.points(userId, type,20L,"邀请了用户："+phUserInfo.getId());
            return jsonResultHelper.buildSuccessJsonResult(null);
        }else{
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.POINTS_LIMITED);
        }
    }

    @ApiOperation(value = "阅读文章")
    @PostMapping("/reading")
    public JsonResult reading(
            @ApiParam(value = "用户ID",required = true) @RequestParam Long userId,
            @ApiParam(value = "阅读时长[单位：秒]",required = true) @RequestParam int second){
        try {

            //获得已经阅读文章的时长
            String secondString =stringRedisTemplate.opsForValue().get(READING_KEY+"_"+userId+"_"+ DateFormatUtils.format(new Date(),"yyyy-MM-dd"));
            int nowSecond = Integer.parseInt(StringUtils.isBlank(secondString)?"0":secondString);

            //进行业务处理
            //判断阅读时间是否超出上限
            if (nowSecond>=1500){
                return jsonResultHelper.buildFailJsonResult(CommonResultCode.POINTS_LIMITED);
            }
            //超出上限则给出默认值
            if (second > 1500-nowSecond){
                second = 1500-nowSecond;
            }
            //计算出需要增加几次积分
            int remainder =nowSecond%300;
            remainder += second;
            int addPoints = remainder/300;
            nowSecond += second;
            stringRedisTemplate.opsForValue().set(READING_KEY+"_"+userId+"_"+ DateFormatUtils.format(new Date(),"yyyy-MM-dd"),String.valueOf(nowSecond),1, TimeUnit.DAYS);
            //返回处理结果
            if (addPoints >0){
                phAccumulatePointsService.points(userId, "1",(long)(30*addPoints),null);
            }
            return jsonResultHelper.buildSuccessJsonResult(null);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("阅读文章异常："+e);
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
        }
    }
}
