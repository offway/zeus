package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhArticle;
import java.lang.String;
import java.util.List;

/**
 * 文章Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public interface PhArticleRepository extends JpaRepository<PhArticle,Long>,JpaSpecificationExecutor<PhArticle> {

	@Query(nativeQuery=true,value="select * from ph_article where status='1' and id !=?3 and type=?1 order by create_time desc limit ?2")
	List<PhArticle> findRecommendByType(String type,int limit,Long id);
	
	@Query(nativeQuery=true,value="select * from ph_article where status='1' and id !=?3 and type=?1 and tag=?4 order by create_time desc limit ?2")
	List<PhArticle> findRecommendByTypeAndTag(String type, int limit,Long id,String tag);
}
