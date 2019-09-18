package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhActivityComments;

/**
 * 文章评论Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-09-18 16:05:55 Exp $
 */
public interface PhActivityCommentsRepository extends JpaRepository<PhActivityComments,Long>,JpaSpecificationExecutor<PhActivityComments> {

	/** 此处写一些自定义的方法 **/


}
