package cn.offway.zeus.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.offway.zeus.domain.PhArticle;
import cn.offway.zeus.dto.ArticleDto;

/**
 * 文章Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhArticleService{

	PhArticle save(PhArticle phArticle);
	
	PhArticle getOne(Long id);

	Page<PhArticle> findByPage(ArticleDto articleDto, Pageable page);

	List<PhArticle> findRecommendByType(String type, int limit,Long id);

	List<PhArticle> findRecommendByTypeAndTag(String type, int limit, Long id, String tag);
}
