package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaBuilder.In;

import org.apache.commons.lang3.StringUtils;
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

import cn.offway.zeus.service.PhOrderInfoService;
import cn.offway.zeus.service.PhPreorderInfoService;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.domain.PhOrderGoods;
import cn.offway.zeus.domain.PhOrderInfo;
import cn.offway.zeus.domain.PhPreorderInfo;
import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.repository.PhGoodsStockRepository;
import cn.offway.zeus.repository.PhOrderGoodsRepository;
import cn.offway.zeus.repository.PhOrderInfoRepository;
import cn.offway.zeus.repository.PhPreorderInfoRepository;
import cn.offway.zeus.repository.PhVoucherInfoRepository;


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
	public int countByUserIdAndStatus(Long userId,String status){
		return phPreorderInfoRepository.countByUserIdAndStatus(userId, status);
	}
	
	@Override
	public void cancelOrder(String preorderNo) throws Exception{
		failResult(preorderNo,"");
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
	public void alipay(String status,String preorderNo) throws Exception {
		/**
		 * 	WAIT_BUYER_PAY	交易创建，等待买家付款
			TRADE_CLOSED	未付款交易超时关闭，或支付完成后全额退款
			TRADE_SUCCESS	交易支付成功
			TRADE_FINISHED	交易结束，不可退款
		 */
		if("TRADE_SUCCESS".equals(status)){
			successResult(preorderNo,"alipay");
		}else if("TRADE_CLOSED".equals(status)){
			failResult(preorderNo,"alipay");
		}
		
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = Exception.class)
	public void wxpay(String status,String preorderNo) throws Exception {
		/**
			SUCCESS	交易支付成功
			FAIL	失败
		 */
		if("SUCCESS".equals(status)){
			successResult(preorderNo,"wxpay");
		}else if("FAIL".equals(status)){
			failResult(preorderNo,"wxpay");
		}
		
	}

	private void failResult(String preorderNo,String payChannel) throws Exception {
		PhPreorderInfo phPreorderInfo = findByOrderNoAndStatus(preorderNo, "0");
		if(null != phPreorderInfo){
			phPreorderInfo.setStatus("2");//交易关闭
			phPreorderInfo.setPayChannel(payChannel);
			save(phPreorderInfo);
			
			//退余额
			Double walletAmount = phPreorderInfo.getWalletAmount();
			if(null != walletAmount){
				Long userId = phPreorderInfo.getUserId();
				PhUserInfo phUserInfo = phUserInfoService.findOne(userId);
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
			phVoucherInfoRepository.back(pvoucherIds);
			
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
		}
	}
}
