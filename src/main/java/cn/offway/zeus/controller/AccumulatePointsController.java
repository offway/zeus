package cn.offway.zeus.controller;

import cn.offway.zeus.domain.PhAccumulatePoints;
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
import org.springframework.data.domain.Page;
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

    @ApiOperation(value = "分享文章邀请注册")
    @PostMapping("/points")
    public JsonResult points(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "类型：2-分享文章,3-邀请好友完成注册", required = true) @RequestParam String type) {
        try {
            switch (type) {
                case "2":
                    if (phAccumulatePointsService.countByUserIdAndTypeToday(userId, type) > 0) {
                        return jsonResultHelper.buildFailJsonResult(CommonResultCode.POINTS_LIMITED);
                    }
                    break;
                case "3":
                    if (phAccumulatePointsService.countByUserIdAndTypeToday(userId, type) >= 10) {
                        return jsonResultHelper.buildFailJsonResult(CommonResultCode.POINTS_LIMITED);
                    }
                    break;
                default:
                    return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
            }
            phAccumulatePointsService.points(userId, type);
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("分享文章邀请注册异常：",e);
            return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
        }
    }
}
