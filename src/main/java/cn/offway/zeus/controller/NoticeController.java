package cn.offway.zeus.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.offway.zeus.domain.PhNotice;
import cn.offway.zeus.repository.PhNoticeRepository;
import cn.offway.zeus.service.PhNoticeService;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags={"通知"})
@RestController
@RequestMapping("/notice")
public class NoticeController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhNoticeService phNoticeService;
	
	
	
	@ApiOperation("通知首页数据")
	@GetMapping("/index")
	public JsonResult index(@ApiParam("用户ID") @RequestParam Long userId){
		
//		String str = "[{\"type\":\"0\",\"content\":\"#CONTENT0#\"},{\"type\":\"1\",\"content\":\"#CONTENT1#\"}]";
		List<PhNotice> notice = phNoticeService.findUserAll();
        List<PhNotice> notices = phNoticeService.findNoticeIndex(userId);
        for (PhNotice phNotice : notice) {
            phNotice.setUserId(userId);
            notices.add(phNotice);
        }
		/*for (PhNotice phNotice : notices) {
			str.replace("CONTENT"+phNotice.getType(), phNotice.getContent());
		}*/
		return jsonResultHelper.buildSuccessJsonResult(notices);
	}
	
	@ApiOperation("通知列表")
	@GetMapping("/list")
	public JsonResult index(
			@ApiParam("用户ID") @RequestParam(required = false) Long userId,
			@ApiParam("类型[0-系统消息，1-订单通知，2-活动通知]") @RequestParam String type,
			@ApiParam("页码,从0开始") @RequestParam int page,
			@ApiParam("页大小") @RequestParam int size){
		if(null != userId) phNoticeService.read(type,userId);
		return jsonResultHelper.buildSuccessJsonResult(phNoticeService.findByPage(type, userId, PageRequest.of(page,size)));
	}
	
	
}
