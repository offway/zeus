package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhShoppingCartService;

import cn.offway.zeus.domain.PhShoppingCart;
import cn.offway.zeus.repository.PhShoppingCartRepository;


/**
 * 购物车Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhShoppingCartServiceImpl implements PhShoppingCartService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhShoppingCartRepository phShoppingCartRepository;
	
	@Override
	public PhShoppingCart save(PhShoppingCart phShoppingCart){
		return phShoppingCartRepository.save(phShoppingCart);
	}
	
	@Override
	public PhShoppingCart findOne(Long id){
		return phShoppingCartRepository.findOne(id);
	}
}
