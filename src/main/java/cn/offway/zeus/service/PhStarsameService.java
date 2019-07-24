package cn.offway.zeus.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.offway.zeus.domain.PhStarsame;

/**
 * 明星同款Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhStarsameService{

	PhStarsame save(PhStarsame phStarsame);
	
	PhStarsame getOne(Long id);

	List<PhStarsame> indexData();

	int praise(Long id);
	
	int praisecancel(Long id);

	Page<PhStarsame> findByPage(String starName, Pageable page);
}
