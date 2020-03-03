package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhTheme;

import java.util.List;

/**
 * 主题列表Repository接口
 *
 * @author tbw
 * @version $v: 1.0.0, $time:2020-03-03 13:55:07 Exp $
 */
public interface PhThemeRepository extends JpaRepository<PhTheme,Long>,JpaSpecificationExecutor<PhTheme> {

    List<PhTheme> findByIsRecommend(Long status);
}
