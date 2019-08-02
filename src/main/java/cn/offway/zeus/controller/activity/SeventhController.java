package cn.offway.zeus.controller.activity;

import cn.offway.zeus.domain.PhConfig;
import cn.offway.zeus.domain.PhSeventh;
import cn.offway.zeus.repository.PhSeventhRecordRepository;
import cn.offway.zeus.repository.PhSeventhRepository;
import cn.offway.zeus.repository.PhVoucherInfoRepository;
import cn.offway.zeus.service.PhConfigService;
import cn.offway.zeus.service.PhSeventhService;
import cn.offway.zeus.service.PhVoucherInfoService;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(tags={"七夕节活动"})
@RestController
@RequestMapping("/activity/seventh")
public class SeventhController {

    @Autowired
    private PhSeventhService phSeventhService;

    @Autowired
    private JsonResultHelper jsonResultHelper;

    @Autowired
    private PhSeventhRepository phSeventhRepository;

    @Autowired
    private PhSeventhRecordRepository phSeventhRecordRepository;

    @Autowired
    private PhVoucherInfoRepository phVoucherInfoRepository;

    @Autowired
    private PhConfigService phConfigService;

    @ApiOperation(value = "初始化")
    @GetMapping("/init")
    public JsonResult init(Long userId){
        Map<String,Object> resultMap = new HashMap<>();
        Long lotteryNum = 0L;
        PhSeventh phSeventh = phSeventhRepository.findByUserId(userId);
        if(null != phSeventh){
            lotteryNum = phSeventh.getLotteryNum();
        }
        resultMap.put("lotteryNum",lotteryNum);
        int c = phSeventhRecordRepository.countByShareUserIdAndCreateDateAndType(userId, DateFormatUtils.format(new Date(),"yyyy-MM-dd"),"0");
        resultMap.put("isShareForToday",c>0);

        List<Long> voucherProjectIds = new ArrayList<>();

        List<PhConfig> configs = phConfigService.findByNameIn("VP_QIXI_10","VP_QIXI_30","VP_QIXI_50","VP_QIXI_100");
        for (PhConfig config : configs) {
            voucherProjectIds.add(Long.parseLong(config.getContent()));
        }
        resultMap.put("luckies",phVoucherInfoRepository.findByUserIdAndVoucherProjectIdInOrderByCreateTimeDesc(userId,voucherProjectIds));
        return jsonResultHelper.buildSuccessJsonResult(resultMap);
    }

    @ApiOperation(value = "用户分享领券")
    @PostMapping("/share")
    public JsonResult share(@ApiParam("用户ID") @RequestParam Long userId){
        phSeventhService.share(userId);
        return jsonResultHelper.buildSuccessJsonResult(null);
    }

    @ApiOperation(value = "好友点击后送券及抽奖机会")
    @PostMapping("/receive")
    public JsonResult receive(
            @ApiParam("分享用户ID") @RequestParam Long shareUserId,
            @ApiParam("被邀请用户ID") @RequestParam Long receiveUserId,
            @ApiParam("推送URL") @RequestParam String pushUrl){
        phSeventhService.receive(shareUserId, receiveUserId,pushUrl);
        return jsonResultHelper.buildSuccessJsonResult(null);

    }

    @ApiOperation(value = "抽奖")
    @PostMapping("/lottery")
    public JsonResult lottery(@ApiParam("用户ID") @RequestParam Long userId){
        return jsonResultHelper.buildSuccessJsonResult(phSeventhService.lottery(userId));

    }
}
