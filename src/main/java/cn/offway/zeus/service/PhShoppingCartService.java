package cn.offway.zeus.service;

import java.util.List;
import java.util.Map;

import cn.offway.zeus.domain.PhShoppingCart;
import cn.offway.zeus.dto.OrderInitDto;
import cn.offway.zeus.utils.JsonResult;

/**
 * 购物车Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhShoppingCartService{

	PhShoppingCart save(PhShoppingCart phShoppingCart);
	
	PhShoppingCart findById(Long id);

    JsonResult shopingCarListV2(Long userId,List<Long> scIds);

    JsonResult shopingCarList(Long userId);

	void delete(List<Long> ids);

	JsonResult orderInitCheck(OrderInitDto orderInitDto);

	JsonResult orderInit(OrderInitDto orderInitDto);

	int deleteByStockIds(List<Long> ids);

	JsonResult shopingCar(Long userId, Long stockId, Long goodsCount, String type);
}
