package cn.offway.zeus.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.offway.zeus.domain.PhNotice;

/**
 * 消息通知Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhNoticeService{

	PhNotice save(PhNotice phNotice);
	
	PhNotice findById(Long id);

	Page<PhNotice> findByPage(String type, Long userId, Pageable page);

	List<PhNotice> findNoticeIndex(Long userId);

	int countByUserIdAndIsRead(Long userId, String isRead);

	List<PhNotice> findUserAll();

	List<String> findTypes();

	int read(String type, Long userId);
}
