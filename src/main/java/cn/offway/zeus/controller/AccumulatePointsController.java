package cn.offway.zeus.controller;

import cn.offway.zeus.service.PhAccumulatePointsService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;


@Api(tags={"积分"})
@RestController
@RequestMapping("/accumulatePoints")
public class AccumulatePointsController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JsonResultHelper jsonResultHelper;

    @Autowired
    private PhAccumulatePointsService phAccumulatePointsService;

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

        boolean signed = phAccumulatePointsService.countByUserIdToday(userId);
        if(signed){
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
    public JsonResult list(
            @ApiParam(value = "用户ID",required = true) @RequestParam Long userId,
            @ApiParam(value = "页码,从0开始",required = true) @RequestParam int page,
            @ApiParam(value = "页大小",required = true) @RequestParam int size){
        return jsonResultHelper.buildSuccessJsonResult(phAccumulatePointsService.finByPage(userId, PageRequest.of(page, size)));
    }
}
