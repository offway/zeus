package cn.offway.zeus.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.offway.zeus.service.PhOrderInfoService;
import cn.offway.zeus.service.PhPreorderInfoService;

@Component
public class ScheduledService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PhPreorderInfoService phPreorderInfoService;
	/**
	 * 订单超时关闭
	 */
	@Scheduled(cron = "0 0/5 * * * ?")
	public void dailyOpen() {
		logger.info("订单超时关闭定时任务开始");
		try {
			
			List<String> orderNos = phPreorderInfoService.orderTimeOut();
			for (String orderNo : orderNos) {
				logger.info("订单超时关闭定时任务处理订单号："+orderNo);
				phPreorderInfoService.cancelOrder(orderNo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("订单超时关闭定时任务异常",e);
		}
		logger.info("订单超时关闭定时任务结束");
	}
}
