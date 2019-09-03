package cn.offway.zeus.service;


import java.util.List;
import java.util.Map;

import cn.offway.zeus.domain.PhAccumulatePoints;

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

    boolean countByUserIdToday(Long userId);

    Map<String,Object> init(Long userId);

    Map<String, Object> sign(Long userId) throws Exception;
}
