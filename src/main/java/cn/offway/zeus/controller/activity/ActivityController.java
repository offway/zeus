package cn.offway.zeus.controller.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhLaborPrize;
import cn.offway.zeus.repository.PhLaborPrizeRepository;
import cn.offway.zeus.service.PhVoucherInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags={"活动"})
@RestController
@RequestMapping("/activity")
public class ActivityController {

	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhLaborPrizeRepository phLaborPrizeRepository;
	
	@Autowired
	private PhVoucherInfoService phVoucherInfoService;
	
	@ApiOperation(value = "奖品兑换")
	@PostMapping("/exchange")
	public JsonResult exchange(
			@ApiParam("用户ID") @RequestParam Long userId,
			@ApiParam("抽奖码") @RequestParam String code){
		
		String name = null;
		PhLaborPrize phLaborPrize = phLaborPrizeRepository.findByTypeAndStatusAndRemark("2", "0", code);
		if(null!=phLaborPrize){
			phLaborPrize.setStatus("1");
			phLaborPrizeRepository.save(phLaborPrize);
			name = phLaborPrize.getName();
			if(!"猫咪便签本".equals(name)){
				Long vpid = phLaborPrize.getVoucherProjectId();
				if(null!=vpid){
					boolean result = phVoucherInfoService.giveVoucher(userId, vpid);
					if(result){
						return jsonResultHelper.buildSuccessJsonResult(name);
					}else{
						return jsonResultHelper.buildFailJsonResult(CommonResultCode.SYSTEM_ERROR);
					}
				}
			}
		}
		
		return jsonResultHelper.buildSuccessJsonResult(name);
	}
	
	@ApiOperation(value = "潮流展抽奖")
	@PostMapping("/lottery/exhibition")
	public JsonResult exhibition(){
		
		String name = null;
		PhLaborPrize phLaborPrize = phLaborPrizeRepository.lottery("3");
		if(null!=phLaborPrize){
			phLaborPrize.setStatus("1");
			phLaborPrizeRepository.save(phLaborPrize);
			name = phLaborPrize.getName();
		}
		
		return jsonResultHelper.buildSuccessJsonResult(name);
	}
}
