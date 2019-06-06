package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import cn.offway.zeus.service.PhAddressService;
import cn.offway.zeus.service.PhMerchantService;
import cn.offway.zeus.service.PhOrderGoodsService;
import cn.offway.zeus.service.PhOrderInfoService;
import cn.offway.zeus.service.PhRefundGoodsService;
import cn.offway.zeus.service.PhRefundService;
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
	
	@Override
	public PhRefund save(PhRefund phRefund){
		return phRefundRepository.save(phRefund);
	}
	
	@Override
	public PhRefund findOne(Long id){
		return phRefundRepository.findOne(id);
	}
	
	@Override
	public void delete(Long id){
		phRefundRepository.delete(id);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {Exception.class,StockException.class})
	public JsonResult apply(RefundDto refundDto) throws Exception{
		
		String orderNo = refundDto.getOrderNo();

		List<RefundGoodsDto> goodsDtos = refundDto.getGoods();
		PhOrderInfo phOrderInfo = phOrderInfoService.findByOrderNo(orderNo);
		if(phOrderInfo.getUserId() != refundDto.getUserId()){
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.PARAM_ERROR);
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
		if(null != phOrderInfo.getPVoucherId() || null != phOrderInfo.getMVoucherId()|| null != phOrderInfo.getWalletAmount()|| 0D==phOrderInfo.getWalletAmount().doubleValue() || goodsDtos.isEmpty()){
			isComplete = "1";
		}
		
		PhRefund phRefund = new PhRefund();
		
		Long id = refundDto.getId();
		if(null != id){
			delete(id);
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
		
		if("0".equals(isComplete)){
			double amount = 0D;
			Long refundId = phRefund.getId();
			
			List<PhOrderGoods> phOrderGoodss = phOrderGoodsService.findByOrderNo(orderNo);
			
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
						amount = MathUtils.add(amount, MathUtils.div(phOrderGoods.getPrice(), orderGoodsCount,2)*phRefundGoods.getGoodsCount());
						phRefundGoodss.add(phRefundGoods);

					}
				}
			}

				
				
			/*for (PhOrderGoods phOrderGoods : phOrderGoodss) {
				Long orderGoodsId = phOrderGoods.getId();
				PhRefundGoods phRefundGoods = new PhRefundGoods();
				phRefundGoods.setCreateTime(now);
				Long orderGoodsCount = phOrderGoods.getGoodsCount();
				Long count = phRefundGoodsService.refundGoodsCount(orderGoodsId);
				phRefundGoods.setGoodsCount(orderGoodsCount.longValue()-count.longValue());//减去已经退款的数量
				phRefundGoods.setOrderGoodsId(orderGoodsId);
				phRefundGoods.setRefundId(refundId);
				phRefundGoods.setVersion(0L);
				for (RefundGoodsDto refundGoodsDto : goodsDtos) {
					if(orderGoodsId.longValue() == refundGoodsDto.getOrderGoodsId().longValue()){
						phRefundGoods.setGoodsCount(refundGoodsDto.getGoodsCount());
					}
				}
				if(phRefundGoods.getGoodsCount().longValue()<=0L){
					throw new Exception("可退款商品数不足");
				}
				
				amount = MathUtils.add(amount, MathUtils.div(phOrderGoods.getPrice(), orderGoodsCount,2)*phRefundGoods.getGoodsCount());
				phRefundGoodss.add(phRefundGoods);
			}*/
			
			phRefundGoodsService.save(phRefundGoodss);
			
			phRefund.setAmount(amount);
			phRefund = save(phRefund);
		}

		return jsonResultHelper.buildSuccessJsonResult(null);
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
		resultMap.put("orderInfo", phOrderInfo);
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
		PhRefund phRefund = findOne(id);
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
				map.put("property", phOrderGoods.getRemark());
				goods.add(map);
			}
		}else{
			List<PhRefundGoods> phRefundGoodss = phRefundGoodsService.findByRefundId(id);
			for (PhRefundGoods phRefundGoods : phRefundGoodss) {
				PhOrderGoods phOrderGoods = phOrderGoodsService.findOne(phRefundGoods.getOrderGoodsId());
				Map<String, Object> map = new HashMap<>();
				map.put("image", phOrderGoods.getGoodsImage());
				map.put("name", phOrderGoods.getGoodsName());
				map.put("count", phRefundGoods.getGoodsCount());
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
			PhMerchant phMerchant = phMerchantService.findOne(merchantId);
			Long retAddrId = phMerchant.getReturnAddrId();
			resultMap.put("addr", phAddressService.findOne(retAddrId));
		}

		
		
		return jsonResultHelper.buildSuccessJsonResult(resultMap);

	}

}
