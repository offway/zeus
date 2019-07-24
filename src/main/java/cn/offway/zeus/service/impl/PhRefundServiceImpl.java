package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaBuilder.In;

import cn.offway.zeus.repository.PhOrderInfoRepository;
import cn.offway.zeus.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import cn.offway.zeus.domain.PhMerchant;
import cn.offway.zeus.domain.PhOrderGoods;
import cn.offway.zeus.domain.PhOrderInfo;
import cn.offway.zeus.domain.PhRefund;
import cn.offway.zeus.domain.PhRefundGoods;
import cn.offway.zeus.dto.RefundDto;
import cn.offway.zeus.dto.RefundGoodsDto;
import cn.offway.zeus.exception.StockException;
import cn.offway.zeus.repository.PhRefundRepository;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import cn.offway.zeus.utils.MathUtils;
import io.swagger.annotations.ApiParam;


/**
 * 退款/退货Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhRefundServiceImpl implements PhRefundService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhRefundRepository phRefundRepository;
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhOrderGoodsService phOrderGoodsService;
	
	@Autowired
	private PhRefundGoodsService phRefundGoodsService;
	
	@Autowired
	private PhOrderInfoService phOrderInfoService;
	
	@Autowired
	private PhMerchantService phMerchantService;
	
	@Autowired
	private PhAddressService phAddressService;

	@Autowired
	private SmsService smsService;

	@Autowired
	private PhOrderInfoRepository phOrderInfoRepository;
	
	@Override
	public PhRefund save(PhRefund phRefund){
		return phRefundRepository.save(phRefund);
	}
	
	@Override
	public PhRefund getOne(Long id){
		return phRefundRepository.getOne(id);
	}
	
	@Override
	public void deleteById(Long id){
		phRefundRepository.deleteById(id);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {Exception.class,StockException.class})
	public JsonResult apply(RefundDto refundDto) throws Exception{
		
		String orderNo = refundDto.getOrderNo();

		List<RefundGoodsDto> goodsDtos = refundDto.getGoods();
		PhOrderInfo phOrderInfo = phOrderInfoService.findByOrderNo(orderNo);
		if(phOrderInfo.getUserId().longValue() != refundDto.getUserId().longValue()){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
		}
		
		if(null != phOrderInfo.getReceiptTime()){
			if(DateUtils.addDays(phOrderInfo.getReceiptTime(), 7).before(new Date())){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.REFUND_TIMEOUT);
			}
		}
		
		
		List<String> statuss = new ArrayList<>();
		//1-已付款,2-已发货,3-已收货
		statuss.add("1");
		statuss.add("2");
		statuss.add("3");
		if(!statuss.contains(phOrderInfo.getStatus())){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);

		}
		
		String isComplete = "0";
		if(null != phOrderInfo.getPVoucherId() || null != phOrderInfo.getMVoucherId()|| 
				phOrderInfo.getWalletAmount().doubleValue()>0D || goodsDtos.isEmpty()){
			isComplete = "1";
		}
		
		PhRefund phRefund = new PhRefund();
		
		Long id = refundDto.getId();
		if(null != id){
			deleteById(id);
			phRefundGoodsService.deleteByRefundId(id);
		}
		
		//一笔订单只能提交一次退款申请
		int c = phRefundRepository.isCompleteOrderNo(orderNo);//查询该订单是否已整单退款-暂时不用
		if(c>0){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.REFUND_APPLIED);
		}
		
		Date now = new Date();
		
		phRefund.setContent(refundDto.getContent());
		phRefund.setImage(refundDto.getImage());
		phRefund.setOrderNo(orderNo);
		phRefund.setReason(refundDto.getReason());
		phRefund.setType(refundDto.getType());
		phRefund.setUserId(refundDto.getUserId());
		phRefund.setVersion(0L);
		phRefund.setStatus("0");
		phRefund.setCreateTime(now);
		phRefund.setIsComplete(isComplete);
		phRefund.setAmount(phOrderInfo.getAmount());
		phRefund = save(phRefund);
		
		Long goodsNum = 0L;//退款商品数
		
		List<PhOrderGoods> phOrderGoodss = phOrderGoodsService.findByOrderNo(orderNo);

		
		if("0".equals(isComplete)){
			double amount = 0D;
			Long refundId = phRefund.getId();
			
			List<PhRefundGoods> phRefundGoodss = new ArrayList<>();
			
			for (RefundGoodsDto refundGoodsDto : goodsDtos) {
				for (PhOrderGoods phOrderGoods : phOrderGoodss) {
					Long orderGoodsId = phOrderGoods.getId();
					if(orderGoodsId.longValue() == refundGoodsDto.getOrderGoodsId().longValue()){
						PhRefundGoods phRefundGoods = new PhRefundGoods();
						phRefundGoods.setCreateTime(now);
						Long orderGoodsCount = phOrderGoods.getGoodsCount();
						Long count = phRefundGoodsService.refundGoodsCount(orderGoodsId);
						phRefundGoods.setOrderGoodsId(orderGoodsId);
						phRefundGoods.setRefundId(refundId);
						phRefundGoods.setVersion(0L);
						//购买数量 -已经退款的数量<要退款数量
						if(orderGoodsCount.longValue()-count.longValue()<refundGoodsDto.getGoodsCount().longValue()){
							throw new Exception("可退款商品数不足");
						}
						phRefundGoods.setGoodsCount(refundGoodsDto.getGoodsCount());
						//退款金额使用优惠后实付金额
						double price = null == phOrderGoods.getAmount()?phOrderGoods.getPrice():phOrderGoods.getAmount();
						amount = MathUtils.add(amount, MathUtils.div(price, orderGoodsCount,2)*phRefundGoods.getGoodsCount());
						phRefundGoodss.add(phRefundGoods);
						goodsNum+=phRefundGoods.getGoodsCount();
					}
				}
			}
			
			if(MathUtils.add(amount, null == phOrderInfo.getMailFee()?0D:phOrderInfo.getMailFee())==phOrderInfo.getAmount().doubleValue()){
				phRefund.setAmount(phOrderInfo.getAmount());
				phRefund.setIsComplete("1");
			}else{
				phRefund.setAmount(amount);
				phRefundGoodsService.save(phRefundGoodss);

			}
		}else{
			for (PhOrderGoods phOrderGoods : phOrderGoodss) {
				goodsNum+=phOrderGoods.getGoodsCount();
			}

		}
		
		if("1".equals(phRefund.getType())){
			//退货退款
			if(phRefund.getReason().equals("尺码拍错/不喜欢/效果差") || phRefund.getReason().equals("其他")){
				if("1".equals(phRefund.getIsComplete())){
					phRefund.setAmount(MathUtils.sub(phRefund.getAmount(), null == phOrderInfo.getMailFee()?0D:phOrderInfo.getMailFee()));
				}
			}
		}
		
		phRefund.setGoodsCount(goodsNum);
		phRefund = save(phRefund);

		try {
			//短信通知商户
			String message = "【很潮】提醒您：亲，您有一笔退款申请！请及时登陆后台审核！";
			//0-仅退款,1-退货退款,2-换货
			if("1".equals(phRefund.getType())){
				message = "【很潮】提醒您：亲，您有一笔退货退款申请！请及时登陆后台审核！";
			}
			String phone = phOrderInfoRepository.findMerchantPhoneByOrderNo(orderNo);
			smsService.sendMsg(phone, message);
			smsService.sendMsg("15001775461", message);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("退款申请成功短信通知商户异常orderNo="+orderNo);
		}
		return jsonResultHelper.buildSuccessJsonResult(phRefund.getId());
	}
	
	@Override
	public String canRefund(String orderNo){
		String canRefund = "";
		//一笔订单只能提交一次退款申请
		int c = phRefundRepository.isCompleteOrderNo(orderNo);//查询该订单是否已整单退款-暂时不用
		if(c>0){
			canRefund = CommonResultCode.REFUND_APPLIED.getStatusCode();
		}
		
		PhOrderInfo phOrderInfo = phOrderInfoService.findByOrderNo(orderNo);

		if(null != phOrderInfo.getReceiptTime()){
			if(DateUtils.addDays(phOrderInfo.getReceiptTime(), 7).before(new Date())){
				canRefund = CommonResultCode.REFUND_TIMEOUT.getStatusCode();
			}
		}
		
		return canRefund;
	}
	
	@Override
	public JsonResult init(String orderNo){
		
		//一笔订单只能提交一次退款申请
		int c = phRefundRepository.isCompleteOrderNo(orderNo);//查询该订单是否已整单退款-暂时不用
		if(c>0){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.REFUND_APPLIED);
		}
		
		Map<String, Object> resultMap = new HashMap<>();
		PhOrderInfo phOrderInfo = phOrderInfoService.findByOrderNo(orderNo);

		if(null != phOrderInfo.getReceiptTime()){
			if(DateUtils.addDays(phOrderInfo.getReceiptTime(), 7).before(new Date())){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.REFUND_TIMEOUT);
			}
		}
		
		List<String> statuss = new ArrayList<>();
		//1-已付款,2-已发货,3-已收货
		statuss.add("1");
		statuss.add("2");
		statuss.add("3");
		if(!statuss.contains(phOrderInfo.getStatus())){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);

		}
		
		resultMap.put("orderInfo", phOrderInfo);
		resultMap.put("hasCoupon", phOrderInfo.getMVoucherId()!=null || phOrderInfo.getPVoucherId() != null || phOrderInfo.getWalletAmount().doubleValue()>0D);
		List<PhOrderGoods> phOrderGoodss = phOrderGoodsService.findByOrderNo(orderNo);
		
		List<PhOrderGoods> goodss = new ArrayList<>();
		for (PhOrderGoods phOrderGoods : phOrderGoodss) {
			Long orderGoodsId = phOrderGoods.getId();
			Long orderGoodsCount = phOrderGoods.getGoodsCount();
			Long count = phRefundGoodsService.refundGoodsCount(orderGoodsId);
			//购买数量 -已经退款的数量
			long surplus = orderGoodsCount.longValue()-count.longValue();
			if(surplus>0){
				phOrderGoods.setGoodsCount(surplus);
				goodss.add(phOrderGoods);
			}
		}
		
		resultMap.put("orderGoods", goodss);
		
		return jsonResultHelper.buildSuccessJsonResult(resultMap);
	}
	
	@Override
	public JsonResult info(Long id){
		PhRefund phRefund = getOne(id);
		String isComplete = phRefund.getIsComplete();
		String orderNo = phRefund.getOrderNo();
		Map<String, Object> resultMap = new HashMap<>();
		List<Map<String, Object>> goods = new ArrayList<>();
		if("1".equals(isComplete)){
			//整单退款
			List<PhOrderGoods> phOrderGoodss = phOrderGoodsService.findByOrderNo(orderNo);
			for (PhOrderGoods phOrderGoods : phOrderGoodss) {
				Map<String, Object> map = new HashMap<>();
				map.put("image", phOrderGoods.getGoodsImage());
				map.put("name", phOrderGoods.getGoodsName());
				map.put("count", phOrderGoods.getGoodsCount());
				map.put("price", phOrderGoods.getPrice());
				map.put("property", phOrderGoods.getRemark());
				goods.add(map);
			}
		}else{
			List<PhRefundGoods> phRefundGoodss = phRefundGoodsService.findByRefundId(id);
			for (PhRefundGoods phRefundGoods : phRefundGoodss) {
				PhOrderGoods phOrderGoods = phOrderGoodsService.getOne(phRefundGoods.getOrderGoodsId());
				Map<String, Object> map = new HashMap<>();
				map.put("image", phOrderGoods.getGoodsImage());
				map.put("name", phOrderGoods.getGoodsName());
				map.put("count", phRefundGoods.getGoodsCount());
				map.put("price", MathUtils.mul(MathUtils.div(phOrderGoods.getPrice(), phOrderGoods.getGoodsCount(), 2), phRefundGoods.getGoodsCount()));
				map.put("property", phOrderGoods.getRemark());
				goods.add(map);
			}
		}
		
		resultMap.put("goods", goods);
		resultMap.put("refund", phRefund);
		
		//不是仅退款，需要查询退货地址
		if(!"0".equals(phRefund.getType())){
			//退货地址
			PhOrderInfo phOrderInfo = phOrderInfoService.findByOrderNo(orderNo);
			Long merchantId = phOrderInfo.getMerchantId();
			PhMerchant phMerchant = phMerchantService.getOne(merchantId);
			Long retAddrId = phMerchant.getReturnAddrId();
			resultMap.put("addr", phAddressService.getOne(retAddrId));
		}

		
		
		return jsonResultHelper.buildSuccessJsonResult(resultMap);

	}
	
	@Override
	public Page<PhRefund> findByPage(final Long userId,Pageable page){
		return phRefundRepository.findAll(new Specification<PhRefund>() {
			
			@Override
			public Predicate toPredicate(Root<PhRefund> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> params = new ArrayList<Predicate>();
				
				if(null != userId){
					params.add(cb.equal(root.get("userId"), userId));
				}
				
                Predicate[] predicates = new Predicate[params.size()];
                query.where(params.toArray(predicates));
                query.orderBy(cb.desc(root.get("createTime")));
				return null;
			}
		}, page);
	}

	@Override
	public PhRefund findByOrderNoEnd(String orderNo) {
		return phRefundRepository.findByOrderNoEnd(orderNo);
	}
}
