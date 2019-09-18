package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhStarsameComments;

/**
 * 明星同款评论Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-09-18 16:05:55 Exp $
 */
public interface PhStarsameCommentsRepository extends JpaRepository<PhStarsameComments,Long>,JpaSpecificationExecutor<PhStarsameComments> {

	/** 此处写一些自定义的方法 **/


}
