package cn.offway.zeus.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.offway.zeus.domain.VPickGoods;

/**
 * VIEWService接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface VPickGoodsService{

	VPickGoods save(VPickGoods vPickGoods);
	
	VPickGoods findById(Long id);

	Page<VPickGoods> findByPage(Long pickId, Pageable page);
}
