package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhMerchantFile;

/**
 * 商户附件表Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhMerchantFileService{

	PhMerchantFile save(PhMerchantFile phMerchantFile);
	
	PhMerchantFile findOne(Long id);
}
