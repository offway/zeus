package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhLimitedSaleOp;

/**
 * 限量发售用户操作Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhLimitedSaleOpService{

	PhLimitedSaleOp save(PhLimitedSaleOp phLimitedSaleOp);
	
	PhLimitedSaleOp findById(Long id);
}
