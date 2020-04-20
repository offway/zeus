package cn.offway.zeus.service;


import java.util.List;

import cn.offway.zeus.domain.PhThemeGoods;

/**
 * 主题商品表Service接口
 *
 * @author tbw
 * @version $v: 1.0.0, $time:2020-03-03 13:55:07 Exp $
 */
public interface PhThemeGoodsService{

    PhThemeGoods save(PhThemeGoods phThemeGoods);
	
    PhThemeGoods findOne(Long id);

    void delete(Long id);

    List<PhThemeGoods> save(List<PhThemeGoods> entities);

    List<PhThemeGoods> findByThemeId(Long id);
}
