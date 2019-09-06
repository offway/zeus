package cn.offway.zeus.service;


import java.util.List;
import java.util.Map;

import cn.offway.zeus.domain.PhAccumulatePoints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 积分记录Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-09-03 14:17:13 Exp $
 */
public interface PhAccumulatePointsService{

    PhAccumulatePoints save(PhAccumulatePoints phAccumulatePoints);
	
    PhAccumulatePoints findById(Long id);

    void delete(Long id);

    List<PhAccumulatePoints> save(List<PhAccumulatePoints> entities);

    Page<PhAccumulatePoints> finByPage(Long userId, Pageable pageable);

    boolean countByUserIdToday(Long userId);

    Map<String,Object> init(Long userId);

    Map<String, Object> sign(Long userId) throws Exception;

    int countByUserIdAndTypeToday(Long userId, String type);

    void points(Long userId, String type, Long points, String remark);

}
