package cn.offway.zeus.service;

import cn.offway.zeus.service.PhPreorderInfoService;
import io.netty.util.Timeout;

public class OrderTimeoutService implements io.netty.util.TimerTask{

	private String orderNo;
	
	private PhPreorderInfoService phPreorderInfoService;
	
	@Override
	public void run(Timeout timeout) throws Exception {
		String orderNo = ((OrderTimeoutService)timeout.task()).getOrderNo();
		phPreorderInfoService.cancelOrder(orderNo,"订单超时关闭");
	}

	public OrderTimeoutService() {
		
	}

	public OrderTimeoutService(String orderNo,PhPreorderInfoService phPreorderInfoService) {
		this.setOrderNo(orderNo);
		this.setPhPreorderInfoService(phPreorderInfoService);
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	public PhPreorderInfoService getPhPreorderInfoService() {
		return phPreorderInfoService;
	}
	
	public void setPhPreorderInfoService(PhPreorderInfoService phPreorderInfoService) {
		this.phPreorderInfoService = phPreorderInfoService;
	}
	
}
