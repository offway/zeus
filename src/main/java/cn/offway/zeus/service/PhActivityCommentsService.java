package cn.offway.zeus.service;


import java.util.List;

import cn.offway.zeus.domain.PhActivityComments;

/**
 * 文章评论Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-09-18 16:05:55 Exp $
 */
public interface PhActivityCommentsService{

    PhActivityComments save(PhActivityComments phActivityComments);
	
    PhActivityComments findOne(Long id);

    void delete(Long id);

    List<PhActivityComments> save(List<PhActivityComments> entities);
}
