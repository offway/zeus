package cn.offway.zeus.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhNoticeService;

import cn.offway.zeus.domain.PhNotice;
import cn.offway.zeus.repository.PhNoticeRepository;


/**
 * 消息通知Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Service
public class PhNoticeServiceImpl implements PhNoticeService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhNoticeRepository phNoticeRepository;
	
	@Override
	public PhNotice save(PhNotice phNotice){
		return phNoticeRepository.save(phNotice);
	}
	
	@Override
	public PhNotice findOne(Long id){
		return phNoticeRepository.findOne(id);
	}
}
