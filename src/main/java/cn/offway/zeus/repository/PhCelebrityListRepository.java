package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhCelebrityList;

/**
 * 明星信息表Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2020-03-02 13:55:02 Exp $
 */
public interface PhCelebrityListRepository extends JpaRepository<PhCelebrityList,Long>,JpaSpecificationExecutor<PhCelebrityList> {

	/** 此处写一些自定义的方法 **/
}
