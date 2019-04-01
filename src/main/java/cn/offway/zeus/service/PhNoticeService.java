package cn.offway.zeus.service;

import cn.offway.zeus.domain.PhNotice;

/**
 * 消息通知Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhNoticeService{

	PhNotice save(PhNotice phNotice);
	
	PhNotice findOne(Long id);
}
