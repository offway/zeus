package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhSeventh;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * 七夕活动Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-08-02 14:52:06 Exp $
 */
public interface PhSeventhRepository extends JpaRepository<PhSeventh,Long>,JpaSpecificationExecutor<PhSeventh> {

    PhSeventh findByUserId(Long userId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value="update ph_seventh set lottery_num=lottery_num-1,version=version+1 where user_id =?1 and lottery_num >0")
    int subLotteryNum(Long userId);
}
