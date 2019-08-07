package cn.offway.zeus.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.offway.zeus.domain.PhLimitedSale;
import cn.offway.zeus.dto.LimitedSaleDto;

import java.util.List;

/**
 * 限量发售Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhLimitedSaleService{

	PhLimitedSale save(PhLimitedSale phLimitedSale);
	
	PhLimitedSale findById(Long id);

    List<PhLimitedSale> findHead();

    PhLimitedSale findHeadForEnd();

    Page<PhLimitedSale> findByPage(LimitedSaleDto limitedSaleDto, Pageable page);

	PhLimitedSale findByGoodsId(Long goodsId);
}
