package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhCapitalFlow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 资金流水Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhCapitalFlowService{

	PhCapitalFlow save(PhCapitalFlow phCapitalFlow);
	
	PhCapitalFlow findById(Long id);

	void calculateReturnAmount() throws Exception;

	List<PhCapitalFlow> findByBusinessTypeAndUserIdOrderByCreateTimeDesc(String businesstype, Long userId);

    Page<PhCapitalFlow> findByPage(Long userId, Pageable page);
}
