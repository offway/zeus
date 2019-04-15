package cn.offway.zeus.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.domain.PhMerchant;
import cn.offway.zeus.domain.PhOrderInfo;
import cn.offway.zeus.domain.PhVoucherInfo;
import cn.offway.zeus.dto.OrderAddDto;
import cn.offway.zeus.dto.OrderInitStockDto;
import cn.offway.zeus.dto.OrderMerchantDto;
import cn.offway.zeus.repository.PhOrderInfoRepository;
import cn.offway.zeus.service.PhMerchantService;
import cn.offway.zeus.service.PhOrderInfoService;
import cn.offway.zeus.service.PhVoucherInfoService;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import cn.offway.zeus.utils.MathUtils;


/**
 * 订单Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhOrderInfoServiceImpl implements PhOrderInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhOrderInfoRepository phOrderInfoRepository;
	
	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Autowired
	private PhMerchantService phMerchantService;
	
	@Autowired
	private PhVoucherInfoService phVoucherInfoService;
	
	@Override
	public PhOrderInfo save(PhOrderInfo phOrderInfo){
		return phOrderInfoRepository.save(phOrderInfo);
	}
	
	@Override
	public PhOrderInfo findOne(Long id){
		return phOrderInfoRepository.findOne(id);
	}
	
	@Override
	public String generateOrderNo(String prefix){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String date = sdf.format(new Date());
		String s = prefix+date;
		int countSequence =  phOrderInfoRepository.countSequence(s);
		if(countSequence==0){
			phOrderInfoRepository.sequence(s);
		}
		return phOrderInfoRepository.nextval(s);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = Exception.class)
	public JsonResult add(OrderAddDto orderAddDto) throws Exception{
		Long addrId = orderAddDto.getAddrId();
		Long userId = orderAddDto.getUserId();
		Long pVoucherId = orderAddDto.getVoucherId();
		Double walletAmount = orderAddDto.getWalletAmount();
		
		PhVoucherInfo pphVoucherInfo =  phVoucherInfoService.findOne(pVoucherId);

		Date now = new Date();
		List<OrderMerchantDto> merchantDtos =  orderAddDto.getMerchantDtos();
		for (OrderMerchantDto orderMerchantDto : merchantDtos) {
			
			Long merchantId = orderMerchantDto.getMerchantId();
			Long mVoucherId = orderMerchantDto.getVoucherId();
			List<OrderInitStockDto> stocks = orderMerchantDto.getStocks();
			String message = orderMerchantDto.getMessage();
			PhMerchant phMerchant = phMerchantService.findOne(merchantId);
			PhVoucherInfo mphVoucherInfo =  phVoucherInfoService.findOne(mVoucherId);
			
			String orderNo = generateOrderNo("PH");
			PhOrderInfo phOrderInfo = new PhOrderInfo();
			phOrderInfo.setCreateTime(now);
//			phOrderInfo.setMailFee(mailFee);
			phOrderInfo.setMerchantId(merchantId);
			phOrderInfo.setMerchantLogo(phMerchant.getLogo());
			phOrderInfo.setMerchantName(phMerchant.getName());
			phOrderInfo.setMessage(message);
			phOrderInfo.setMVoucherAmount(mphVoucherInfo.getAmount());
			phOrderInfo.setMVoucherId(mVoucherId);
			phOrderInfo.setOrderNo(orderNo);
//			phOrderInfo.setPrice(price);
//			phOrderInfo.setPVoucherAmount(pVoucherAmount);
			phOrderInfo.setPVoucherId(pVoucherId);
//			phOrderInfo.setWalletAmount(walletAmount1);
			
			phOrderInfo.setAmount(MathUtils.sub(MathUtils.sub(MathUtils.sub(MathUtils.sub(phOrderInfo.getPrice(), phOrderInfo.getMailFee()), phOrderInfo.getMVoucherAmount()), phOrderInfo.getPVoucherAmount()), phOrderInfo.getWalletAmount()));

			phOrderInfo.setStatus("0");
			phOrderInfo.setUserId(userId);
			phOrderInfo.setVersion(0L);
		}
		
		
		return jsonResultHelper.buildSuccessJsonResult(null);
	}
}
