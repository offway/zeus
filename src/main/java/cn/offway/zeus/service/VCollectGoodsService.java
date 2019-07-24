package cn.offway.zeus.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.offway.zeus.domain.VCollectGoods;

/**
 * VIEWService接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface VCollectGoodsService{

	VCollectGoods save(VCollectGoods vCollectGoods);
	
	VCollectGoods getOne(Long id);

	Page<VCollectGoods> findByPage(Long userId, Pageable page);
}
