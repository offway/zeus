package cn.offway.zeus.service;


import java.util.List;

import cn.offway.zeus.domain.PhTheme;

/**
 * 主题列表Service接口
 *
 * @author tbw
 * @version $v: 1.0.0, $time:2020-03-03 13:55:07 Exp $
 */
public interface PhThemeService{

    PhTheme save(PhTheme phTheme);
	
    PhTheme findOne(Long id);

    void delete(Long id);

    List<PhTheme> save(List<PhTheme> entities);

    List<PhTheme> findByIsRecommend(Long status);
}
