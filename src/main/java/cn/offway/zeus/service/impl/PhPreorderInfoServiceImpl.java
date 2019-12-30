package cn.offway.zeus.service.impl;

import cn.offway.zeus.domain.*;
import cn.offway.zeus.repository.*;
import cn.offway.zeus.service.AlipayService;
import cn.offway.zeus.service.PhPreorderInfoService;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.service.SmsService;
import io.growing.sdk.java.GrowingAPI;
import io.growing.sdk.java.dto.GIOEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


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
	private PhOrderInfoRepository phOrderInfoRepository;
	
	@Autowired
	private PhUserInfoService phUserInfoService;
	
	@Autowired
	private PhVoucherInfoRepository phVoucherInfoRepository;
	
	@Autowired
	private PhOrderGoodsRepository phOrderGoodsRepository;
	
	@Autowired
	private PhGoodsStockRepository phGoodsStockRepository;
	
	@Autowired
	private PhCapitalFlowRepository phCapitalFlowRepository;
	
	@Autowired
	private PhGoodsRepository phGoodsRepository;
	
	@Autowired
	private SmsService smsService;

	@Autowired
	private AlipayService alipayService;
	
	@Override
	public PhPreorderInfo save(PhPreorderInfo phPreorderInfo){
		return phPreorderInfoRepository.save(phPreorderInfo);
	}
	
	@Override
	public PhPreorderInfo findById(Long id){
		Optional<PhPreorderInfo> optional = phPreorderInfoRepository.findById(id);
			if (optional.isPresent()){
				return optional.get();
			}
		return null;
	}
	
	@Override
	public PhPreorderInfo findByOrderNoAndStatus(String orderno,String status){
		return phPreorderInfoRepository.findByOrderNoAndStatus(orderno, status);
	}
	
	@Override
	public int countByUserIdAndStatus(Long userId,String status){
		return phPreorderInfoRepository.countByUserIdAndStatus(userId, status);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = Exception.class)
	public void cancelOrder(String preorderNo,String remark) throws Exception{
		failResult(preorderNo,"",remark);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = Exception.class)
	public void returnOrder(String preorderNo) throws Exception{
		returnResult(preorderNo,"");
	}
	
	@Override
	public List<String> orderTimeOut(){
		return phPreorderInfoRepository.orderTimeOut();
	}
	
	@Override
	public Page<PhPreorderInfo> findByPage(final Long userId,Pageable page){
		return phPreorderInfoRepository.findAll(new Specification<PhPreorderInfo>() {
			
			@Override
			public Predicate toPredicate(Root<PhPreorderInfo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				
				params.add(criteriaBuilder.equal(root.get("status"), "0"));
				
				if(null != userId){
					params.add(criteriaBuilder.equal(root.get("userId"), userId));
				}
				
				
                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime")));
				return null;
			}
		}, page);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = Exception.class)
	public void alipay(String status, String preorderNo) throws Exception {
		/*
		 * 	WAIT_BUYER_PAY	交易创建，等待买家付款
			TRADE_CLOSED	未付款交易超时关闭，或支付完成后全额退款
			TRADE_SUCCESS	交易支付成功
			TRADE_FINISHED	交易结束，不可退款
		 */
		PhPreorderInfo phPreorderInfo = findByOrderNoAndStatus(preorderNo, "0");
		String userId = phPreorderInfo == null ? "" : String.valueOf(phPreorderInfo.getUserId());
		if ("TRADE_SUCCESS".equals(status)) {
			//事件行为消息体
			GIOEventMessage eventMessage = new GIOEventMessage.Builder()
					.eventTime(System.currentTimeMillis())            // 事件时间，默认为系统时间（选填）
					.eventKey("orderPaymentSucce")                           // 事件标识 (必填)
					.loginUserId(userId)                   // 登录用户ID (必填)
					.addEventVariable("orderNo", preorderNo)          // 事件级变量 (选填)
					.addEventVariable("payType", 0)          // 事件级变量 (选填)
					.build();
			//上传事件行为消息到服务器
			GrowingAPI.send(eventMessage);
			successResult(preorderNo, "alipay");
		} else if ("TRADE_CLOSED".equals(status)) {
			//事件行为消息体
			GIOEventMessage eventMessage = new GIOEventMessage.Builder()
					.eventTime(System.currentTimeMillis())            // 事件时间，默认为系统时间（选填）
					.eventKey("orderPaymentFailure")                           // 事件标识 (必填)
					.loginUserId(userId)                   // 登录用户ID (必填)
					.addEventVariable("orderNo", preorderNo)          // 事件级变量 (选填)
					.addEventVariable("payType", 0)          // 事件级变量 (选填)
					.build();
			//上传事件行为消息到服务器
			GrowingAPI.send(eventMessage);
			failResult(preorderNo, "alipay", null);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = Exception.class)
	public void wxpay(String status, String preorderNo) throws Exception {
		/*
			SUCCESS	交易支付成功
			FAIL	失败
		 */
		PhPreorderInfo phPreorderInfo = findByOrderNoAndStatus(preorderNo, "0");
		String userId = phPreorderInfo == null ? "" : String.valueOf(phPreorderInfo.getUserId());
		if ("SUCCESS".equals(status)) {
			//事件行为消息体
			GIOEventMessage eventMessage = new GIOEventMessage.Builder()
					.eventTime(System.currentTimeMillis())            // 事件时间，默认为系统时间（选填）
					.eventKey("orderPaymentSucce")                           // 事件标识 (必填)
					.loginUserId(userId)                   // 登录用户ID (必填)
					.addEventVariable("orderNo", preorderNo)          // 事件级变量 (选填)
					.addEventVariable("payType", 1)          // 事件级变量 (选填)
					.build();
			//上传事件行为消息到服务器
			GrowingAPI.send(eventMessage);
			successResult(preorderNo, "wxpay");
		} else if ("FAIL".equals(status)) {
			//事件行为消息体
			GIOEventMessage eventMessage = new GIOEventMessage.Builder()
					.eventTime(System.currentTimeMillis())            // 事件时间，默认为系统时间（选填）
					.eventKey("orderPaymentFailure")                           // 事件标识 (必填)
					.loginUserId(userId)                   // 登录用户ID (必填)
					.addEventVariable("orderNo", preorderNo)          // 事件级变量 (选填)
					.addEventVariable("payType", 1)          // 事件级变量 (选填)
					.build();
			//上传事件行为消息到服务器
			GrowingAPI.send(eventMessage);
			failResult(preorderNo, "wxpay", null);
		}
	}
	
	private void returnResult(String preorderNo,String payChannel) throws Exception {
		PhPreorderInfo phPreorderInfo = phPreorderInfoRepository.findByOrderNo(preorderNo);
		if(null != phPreorderInfo){
			phPreorderInfo.setStatus("2");//交易关闭
			phPreorderInfo.setPayChannel(payChannel);
			save(phPreorderInfo);
			
			//退余额
			Double walletAmount = phPreorderInfo.getWalletAmount();
			if(null != walletAmount){
				Long userId = phPreorderInfo.getUserId();
				PhUserInfo phUserInfo = phUserInfoService.findById(userId);
				phUserInfo.setBalance(phUserInfo.getBalance()+walletAmount);
				phUserInfoService.save(phUserInfo);
			}
			
			/*//退加息券
			List<Long> pvoucherIds = new ArrayList<>(); phPreorderInfo.getPVoucherId();
			if(null != phPreorderInfo.getPVoucherId()){
				pvoucherIds.add(phPreorderInfo.getPVoucherId());
			}
			
			List<PhOrderInfo> orderInfos = phOrderInfoRepository.findByPreorderNoAndStatus(preorderNo, "0");
			for (PhOrderInfo phOrderInfo : orderInfos) {
				if(null != phOrderInfo.getMVoucherId()){
					pvoucherIds.add(phOrderInfo.getMVoucherId());
				}
				
			}
			if(pvoucherIds.size()>0){
				phVoucherInfoRepository.back(pvoucherIds);
			}*/
			
			//更新订单状态
			phOrderInfoRepository.updateStatusByPreOrderNo(preorderNo,"1","4",payChannel);
			
			//恢复库存
			List<PhOrderGoods> phOrderGoodss = phOrderGoodsRepository.findByPreorderNo(preorderNo);
			for (PhOrderGoods phOrderGoods : phOrderGoodss) {
				Long stockId = phOrderGoods.getGoodsStockId();
				Long count = phOrderGoods.getGoodsCount();
				phGoodsStockRepository.addStock(stockId, count);
			}
		}
	}

	private void failResult(String preorderNo,String payChannel,String remark) throws Exception {
		PhPreorderInfo phPreorderInfo = findByOrderNoAndStatus(preorderNo, "0");
		if(null != phPreorderInfo){
			phPreorderInfo.setStatus("2");//交易关闭
			phPreorderInfo.setPayChannel(payChannel);
			phPreorderInfo.setRemark(remark);
			save(phPreorderInfo);
			
			//退余额
			Double walletAmount = phPreorderInfo.getWalletAmount();
			if(null != walletAmount){
				Long userId = phPreorderInfo.getUserId();
				PhUserInfo phUserInfo = phUserInfoService.findById(userId);
				phUserInfo.setBalance(phUserInfo.getBalance()+walletAmount);
				phUserInfoService.save(phUserInfo);
			}
			
			//退加息券
			List<Long> pvoucherIds = new ArrayList<>(); phPreorderInfo.getPVoucherId();
			if(null != phPreorderInfo.getPVoucherId()){
				pvoucherIds.add(phPreorderInfo.getPVoucherId());
			}
			
			List<PhOrderInfo> orderInfos = phOrderInfoRepository.findByPreorderNoAndStatus(preorderNo, "0");
			for (PhOrderInfo phOrderInfo : orderInfos) {
				if(null != phOrderInfo.getMVoucherId()){
					pvoucherIds.add(phOrderInfo.getMVoucherId());
				}
				
			}
			if(pvoucherIds.size()>0){
				phVoucherInfoRepository.back(pvoucherIds);
			}
			
			//更新订单状态
			phOrderInfoRepository.updateStatusByPreOrderNo(preorderNo,"0","4",payChannel);
			
			//恢复库存
			List<PhOrderGoods> phOrderGoodss = phOrderGoodsRepository.findByPreorderNo(preorderNo);
			for (PhOrderGoods phOrderGoods : phOrderGoodss) {
				Long stockId = phOrderGoods.getGoodsStockId();
				Long count = phOrderGoods.getGoodsCount();
				phGoodsStockRepository.addStock(stockId, count);
			}
		}
	}

	private void successResult(String preorderNo,String payChannel) throws Exception  {
		PhPreorderInfo phPreorderInfo = findByOrderNoAndStatus(preorderNo, "0");
		if(null != phPreorderInfo){
			phPreorderInfo.setStatus("1");//已付款
			phPreorderInfo.setPayChannel(payChannel);
			save(phPreorderInfo);
			//更新订单状态
			phOrderInfoRepository.updateStatusByPreOrderNo(preorderNo,"0","1",payChannel);
			
			Double walletAmount = phPreorderInfo.getWalletAmount();
			if(null != walletAmount && walletAmount.doubleValue()>0D){
				//添加资金流水
				PhCapitalFlow phCapitalFlow = new PhCapitalFlow();
				phCapitalFlow.setAmount(walletAmount);
				phCapitalFlow.setBusinessType("1");
				phCapitalFlow.setCreateTime(new Date());
				phCapitalFlow.setOrderNo(preorderNo);
				phCapitalFlow.setType("1");
				phCapitalFlow.setUserId(phPreorderInfo.getUserId());
				phCapitalFlowRepository.save(phCapitalFlow);
			}
			
			//更新商品销量
			List<PhOrderGoods> phOrderGoods =  phOrderGoodsRepository.findByPreorderNo(preorderNo);
			for (PhOrderGoods goods : phOrderGoods) {
				phGoodsRepository.updateSaleCount(goods.getGoodsId(), goods.getGoodsCount());
			}

			try {
				//短信通知商户
				List<Object> phones = phOrderInfoRepository.findMerchantPhone(preorderNo);
				for (Object p : phones) {
					Object[] obj = (Object[]) p;
					smsService.sendMsgBatch(obj[0].toString(), "【很潮】您有一笔新订单，订单编号："+obj[1].toString()+"，商品件数："+obj[2].toString()+"件，请及时登录后台进行发货哦~");
				}
				smsService.sendMsg("15001775461", "【很潮】提醒您：亲，您有一笔新订单来啦！请尽快发货！");
				smsService.sendMsg("15300959951", "【很潮】提醒您：亲，您有一笔新订单来啦！请尽快发货！");
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("短信通知商户异常preorderNo="+preorderNo);
			}
		}
	}


}
