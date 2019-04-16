package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.offway.zeus.service.PhOrderInfoService;
import cn.offway.zeus.service.PhPreorderInfoService;

import cn.offway.zeus.domain.PhPreorderInfo;
import cn.offway.zeus.repository.PhPreorderInfoRepository;


/**
 * 预生成订单Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhPreorderInfoServiceImpl implements PhPreorderInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhPreorderInfoRepository phPreorderInfoRepository;
	
	@Autowired
	private PhOrderInfoService phOrderInfoService;
	
	@Override
	public PhPreorderInfo save(PhPreorderInfo phPreorderInfo){
		return phPreorderInfoRepository.save(phPreorderInfo);
	}
	
	@Override
	public PhPreorderInfo findOne(Long id){
		return phPreorderInfoRepository.findOne(id);
	}
	
	@Override
	public PhPreorderInfo findByOrderNoAndStatus(String orderno,String status){
		return phPreorderInfoRepository.findByOrderNoAndStatus(orderno, status);
	}
	
	@Override
	public void alipay(String status,String preorderNo){
		/**
		 * 	WAIT_BUYER_PAY	交易创建，等待买家付款
			TRADE_CLOSED	未付款交易超时关闭，或支付完成后全额退款
			TRADE_SUCCESS	交易支付成功
			TRADE_FINISHED	交易结束，不可退款
		 */
		if("TRADE_SUCCESS".equals(status)){
			PhPreorderInfo phPreorderInfo = findByOrderNoAndStatus(preorderNo, "0");
			phPreorderInfo.setStatus("1");//已付款
			save(phPreorderInfo);
			
			
			
			
		}else if("TRADE_CLOSED".equals(status)){
			
		}
		
	}
}
