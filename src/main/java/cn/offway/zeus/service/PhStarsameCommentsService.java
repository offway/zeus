package cn.offway.zeus.service;


import java.util.List;

import cn.offway.zeus.domain.PhStarsameComments;

/**
 * 文章评论Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-09-18 16:05:55 Exp $
 */
public interface PhStarsameCommentsService {

    PhStarsameComments save(PhStarsameComments phStarsameComments);
	
    PhStarsameComments findOne(Long id);

    void delete(Long id);

    List<PhStarsameComments> save(List<PhStarsameComments> entities);
}
