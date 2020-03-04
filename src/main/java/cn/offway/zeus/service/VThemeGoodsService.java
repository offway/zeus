package cn.offway.zeus.service;


import java.util.List;

import cn.offway.zeus.domain.VThemeGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * VIEWService接口
 *
 * @author tbw
 * @version $v: 1.0.0, $time:2020-03-04 14:10:13 Exp $
 */
public interface VThemeGoodsService{

    VThemeGoods save(VThemeGoods vThemeGoods);
	
    VThemeGoods findOne(Long id);

    void delete(Long id);

    List<VThemeGoods> save(List<VThemeGoods> entities);

    Page<VThemeGoods> findByPage(Long themeId, Pageable page);
}
