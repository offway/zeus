package cn.offway.zeus.service.impl;

import cn.offway.zeus.domain.PhMerchant;
import cn.offway.zeus.domain.PhOrderInfo;
import cn.offway.zeus.domain.PhSettlementDetail;
import cn.offway.zeus.service.PhMerchantService;
import cn.offway.zeus.service.PhOrderInfoService;
import cn.offway.zeus.service.PhSettlementDetailService;
import cn.offway.zeus.utils.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhSettlementInfoService;

import cn.offway.zeus.domain.PhSettlementInfo;
import cn.offway.zeus.repository.PhSettlementInfoRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 商户结算表Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhSettlementInfoServiceImpl implements PhSettlementInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhSettlementInfoRepository phSettlementInfoRepository;

	@Autowired
	private PhSettlementDetailService phSettlementDetailService;

	@Autowired
	private PhOrderInfoService phOrderInfoService;

	@Autowired
	private PhMerchantService phMerchantService;
	
	@Override
	public PhSettlementInfo save(PhSettlementInfo phSettlementInfo){
		return phSettlementInfoRepository.save(phSettlementInfo);
	}
	
	@Override
	public PhSettlementInfo getOne(Long id){
		return phSettlementInfoRepository.getOne(id);
	}

	@Override
	public void save(String preorderNo) {

		List<PhOrderInfo> phOrderInfos = phOrderInfoService.findByPreorderNoAndStatus(preorderNo,"1");

		List<PhSettlementDetail> phSettlementDetails = new ArrayList<>();
		List<PhSettlementInfo> phSettlementInfos = new ArrayList<>();

		for (PhOrderInfo orderInfo:phOrderInfos) {
			PhSettlementDetail settlementDetail = new PhSettlementDetail();
			settlementDetail.setAmount(orderInfo.getAmount());
			settlementDetail.setCreateTime(new Date());
			Long merchantId = orderInfo.getMerchantId();
			PhMerchant phMerchant = phMerchantService.getOne(merchantId);
			settlementDetail.setCutRate(phMerchant.getRatio());
			settlementDetail.setCutAmount(orderInfo.getAmount() * phMerchant.getRatio() / 100);
			settlementDetail.setMailFee(orderInfo.getMailFee());
			settlementDetail.setMerchantId(orderInfo.getMerchantId());
			settlementDetail.setMerchantLogo(orderInfo.getMerchantLogo());
			settlementDetail.setMerchantName(orderInfo.getMerchantName());
			settlementDetail.setMVoucherAmount(orderInfo.getMVoucherAmount());
			settlementDetail.setPVoucherAmount(orderInfo.getPVoucherAmount());
			settlementDetail.setOrderNo(orderInfo.getOrderNo());
			settlementDetail.setPayChannel(orderInfo.getPayChannel());
			settlementDetail.setPayFee(String.format("%.2f", orderInfo.getAmount() * 0.003));//千分之三的手续费
			settlementDetail.setPrice(orderInfo.getPrice());
			settlementDetail.setWalletAmount(orderInfo.getWalletAmount());
			/* 状态[0-待结算,1-结算中,2-已结算] */
			settlementDetail.setStatus("0");
			settlementDetail.setRemark(orderInfo.getStatus());
			phSettlementDetails.add(settlementDetail);

			PhSettlementInfo settlementInfo = phSettlementInfoRepository.findByMerchantId(merchantId);
			if (null == settlementInfo){
				settlementInfo = new PhSettlementInfo();
				settlementInfo.setMerchantId(phMerchant.getId());
				settlementInfo.setMerchantLogo(phMerchant.getLogo());
				settlementInfo.setMerchantName(phMerchant.getName());
				settlementInfo.setMerchantGoodsCount(0L);
				settlementInfo.setStatisticalTime(new Date());
				settlementInfo.setOrderAmount(0d);
				settlementInfo.setOrderCount(0L);
				settlementInfo.setSettledAmount(0d);
				settlementInfo.setSettledCount(0L);
				settlementInfo.setUnsettledAmount(0d);
				settlementInfo.setUnsettledCount(0L);
			}
			settlementInfo.setOrderAmount(MathUtils.add(settlementDetail.getAmount(),settlementInfo.getOrderAmount()));
			settlementInfo.setOrderCount(settlementInfo.getOrderCount()+1L);
			settlementInfo.setUnsettledAmount(MathUtils.add(settlementInfo.getUnsettledAmount(),settlementDetail.getAmount()));
			settlementInfo.setUnsettledCount(settlementInfo.getUnsettledCount()+1L);
			phSettlementInfos.add(settlementInfo);
		}
		phSettlementDetailService.save(phSettlementDetails);
		phSettlementInfoRepository.saveAll(phSettlementInfos);
	}
}
