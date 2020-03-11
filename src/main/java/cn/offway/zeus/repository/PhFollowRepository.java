package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhFollow;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 关注列表Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2020-03-02 13:55:02 Exp $
 */
public interface PhFollowRepository extends JpaRepository<PhFollow,Long>,JpaSpecificationExecutor<PhFollow> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value="delete from ph_follow where unionid = ?1 and celebrity_id = ?2 ")
    int deleteByUnionidAndCelebrityId(String uid, Long id);

    List<PhFollow> findByUnionid(String unionid);

    PhFollow findByUnionidAndCelebrityId(String unionid,Long aLongcelebrityId);
}
