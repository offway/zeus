package cn.offway.zeus.service;


import java.util.List;

import cn.offway.zeus.domain.PhWithdrawInfo;
import cn.offway.zeus.utils.JsonResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 提现订单Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-07-25 13:18:47 Exp $
 */
public interface PhWithdrawInfoService{

    PhWithdrawInfo save(PhWithdrawInfo phWithdrawInfo);
	
    PhWithdrawInfo findById(Long id);

    void delete(Long id);

    List<PhWithdrawInfo> save(List<PhWithdrawInfo> entities);

    Page<PhWithdrawInfo> findByPage(Long userId, Pageable page);

    JsonResult withdraw(Long userId, double amount);
}
