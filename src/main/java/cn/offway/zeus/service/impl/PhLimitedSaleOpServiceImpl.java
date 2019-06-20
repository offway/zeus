package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhLimitedSaleOpService;

import cn.offway.zeus.domain.PhLimitedSaleOp;
import cn.offway.zeus.repository.PhLimitedSaleOpRepository;


/**
 * 限量发售用户操作Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhLimitedSaleOpServiceImpl implements PhLimitedSaleOpService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhLimitedSaleOpRepository phLimitedSaleOpRepository;
	
	@Override
	public PhLimitedSaleOp save(PhLimitedSaleOp phLimitedSaleOp){
		return phLimitedSaleOpRepository.save(phLimitedSaleOp);
	}
	
	@Override
	public PhLimitedSaleOp findOne(Long id){
		return phLimitedSaleOpRepository.findOne(id);
	}
}
