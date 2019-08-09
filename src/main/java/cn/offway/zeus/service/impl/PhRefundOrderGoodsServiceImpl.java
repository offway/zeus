package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhRefund;
import cn.offway.zeus.domain.PhRefundGoods;
import cn.offway.zeus.repository.PhRefundGoodsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhRefundOrderGoodsService;

import cn.offway.zeus.domain.PhRefundOrderGoods;
import cn.offway.zeus.repository.PhRefundOrderGoodsRepository;


/**
 * 退换货后的订单商品Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-08-01 14:42:53 Exp $
 */
@Service
public class PhRefundOrderGoodsServiceImpl implements PhRefundOrderGoodsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhRefundOrderGoodsRepository phRefundOrderGoodsRepository;

	@Autowired
	private PhRefundGoodsRepository refundGoodsRepository;
	
	@Override
	public PhRefundOrderGoods save(PhRefundOrderGoods phRefundOrderGoods){
		return phRefundOrderGoodsRepository.save(phRefundOrderGoods);
	}
	
	@Override
	public PhRefundOrderGoods findById(Long id){
		Optional<PhRefundOrderGoods> optional =  phRefundOrderGoodsRepository.findById(id);
		return optional.isPresent()?optional.get():null;

	}

	@Override
	public void delete(Long id){
		phRefundOrderGoodsRepository.deleteById(id);
	}

	@Override
	public List<PhRefundOrderGoods> saveAll(List<PhRefundOrderGoods> entities){
		return phRefundOrderGoodsRepository.saveAll(entities);
	}

	public void updateByRefund(PhRefund phRefund){
		String orderNo = phRefund.getOrderNo();
		String isComplete = phRefund.getIsComplete();
		String type = phRefund.getType();
		List<PhRefundGoods> phRefundGoodsList = refundGoodsRepository.findByRefundId(phRefund.getId());
		if("0".equals(isComplete)){
			//部分退换货
			int count = phRefundOrderGoodsRepository.countByOrderNo(orderNo);
			//检查是否发生过退换货
			if(count == 0){
				//同步订单商品
				phRefundOrderGoodsRepository.insertOrderGoods(orderNo);
			}
			for (PhRefundGoods phRefundGoods : phRefundGoodsList) {
				Long orderGoodsId = phRefundGoods.getOrderGoodsId();
				int goodsCount = phRefundGoods.getGoodsCount().intValue();
				//减商品数量
				phRefundOrderGoodsRepository.subGoodsCount(orderNo,phRefundGoods.getFromStockId(),goodsCount);
				if("2".equals(type)){
					//换货
					//增加换货商品
					phRefundOrderGoodsRepository.insertGoodsStock(orderGoodsId);
					//修改换货商品
					phRefundOrderGoodsRepository.updateGoodsStock(orderNo,phRefundGoods.getFromStockId(),goodsCount,phRefundGoods.getToStockId(),phRefundGoods.getToStockImage(),phRefundGoods.getToStockImage());
				}
			}
			//删掉退光的商品
			phRefundOrderGoodsRepository.deleteByNoMore(orderNo);
		}
	}
}
