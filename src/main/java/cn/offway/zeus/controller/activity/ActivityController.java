package cn.offway.zeus.controller.activity;

import cn.offway.zeus.domain.PhLaborPrize;
import cn.offway.zeus.repository.PhLaborPrizeRepository;
import cn.offway.zeus.service.PhGoodsStockService;
import cn.offway.zeus.service.PhVoucherInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(tags = {"活动"})
@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private JsonResultHelper jsonResultHelper;

    @Autowired
    private PhLaborPrizeRepository phLaborPrizeRepository;

    @Autowired
    private PhVoucherInfoService phVoucherInfoService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PhGoodsStockService phGoodsStockService;

    @ApiOperation(value = "奖品兑换")
    @PostMapping("/exchange")
    @Transactional
    public JsonResult exchange(
            @ApiParam("用户ID") @RequestParam Long userId,
            @ApiParam("抽奖码") @RequestParam String code,
            @ApiParam("活动批次") @RequestParam String type) {
        String name = null;
        PhLaborPrize phLaborPrize = phLaborPrizeRepository.findByTypeAndRemark(type, code);
        if (null != phLaborPrize) {
            if ("1".equals(phLaborPrize.getStatus())) {
                return jsonResultHelper.buildFailJsonResult(CommonResultCode.VOUCHER_GIVED);
            }
            phLaborPrize.setStatus("1");
            phLaborPrize.setUserId(userId);
            phLaborPrizeRepository.save(phLaborPrize);
            name = phLaborPrize.getName();
            Long vpid = phLaborPrize.getVoucherProjectId();
            if (null != vpid) {
                boolean result = phVoucherInfoService.giveVoucher(userId, vpid);
                if (result) {
                    return jsonResultHelper.buildSuccessJsonResult(name);
                } else {
                    return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
                }
            }
            return jsonResultHelper.buildSuccessJsonResult(name);
        } else {
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.CODE_ERROR);
        }
    }

    @ApiOperation(value = "潮流展抽奖")
    @PostMapping("/lottery/exhibition")
    public JsonResult exhibition() {

        String name = null;
        PhLaborPrize phLaborPrize = phLaborPrizeRepository.lottery("3");
        if (null != phLaborPrize) {
            phLaborPrize.setStatus("1");
            phLaborPrizeRepository.save(phLaborPrize);
            name = phLaborPrize.getName();
        }

        return jsonResultHelper.buildSuccessJsonResult(name);
    }

    @ApiOperation(value = "chillhigh分享")
    @PostMapping("/chillhigh")
    public JsonResult chillhigh(@ApiParam("用户ID") @RequestParam Long userId) {
        stringRedisTemplate.opsForValue().set("zeus.chillhigh.share." + userId, "0", 10, TimeUnit.DAYS);
        return jsonResultHelper.buildSuccessJsonResult(null);
    }

    @ApiOperation(value = "chillhigh初始化")
    @GetMapping("/chillhigh/init")
    public JsonResult chillhighinit(@ApiParam("用户ID") @RequestParam Long userId) {
        Map<String, Object> map = new HashMap<>();
        String isShare = stringRedisTemplate.opsForValue().get("zeus.chillhigh.share." + userId);
        map.put("isShare", StringUtils.isNotBlank(isShare));
        map.put("now", new Date());
        int stock = phGoodsStockService.sumStock(4465L);
        map.put("isSellOut", stock <= 0);
        return jsonResultHelper.buildSuccessJsonResult(map);
    }
}
