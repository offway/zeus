package cn.offway.zeus.service.impl;

import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhChannelUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhThemeService;

import cn.offway.zeus.domain.PhTheme;
import cn.offway.zeus.repository.PhThemeRepository;


/**
 * 主题列表Service接口实现
 *
 * @author tbw
 * @version $v: 1.0.0, $time:2020-03-03 13:55:07 Exp $
 */
@Service
public class PhThemeServiceImpl implements PhThemeService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhThemeRepository phThemeRepository;
	
	@Override
	public PhTheme save(PhTheme phTheme){
		return phThemeRepository.save(phTheme);
	}
	
	@Override
	public PhTheme findOne(Long id){
		Optional<PhTheme> optional = phThemeRepository.findById(id);
		if (optional.isPresent()){
			return optional.get();
		}
		return null;
	}

	@Override
	public void delete(Long id){
		phThemeRepository.deleteById(id);
	}

	@Override
	public List<PhTheme> save(List<PhTheme> entities){
		return phThemeRepository.saveAll(entities);
	}
}
