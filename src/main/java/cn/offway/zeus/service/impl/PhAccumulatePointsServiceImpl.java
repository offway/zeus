package cn.offway.zeus.service.impl;

import java.text.SimpleDateFormat;
import java.util.*;

import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.exception.StockException;
import cn.offway.zeus.service.PhUserInfoService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhAccumulatePointsService;

import cn.offway.zeus.domain.PhAccumulatePoints;
import cn.offway.zeus.repository.PhAccumulatePointsRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


/**
 * 积分记录Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-09-03 14:17:13 Exp $
 */
@Service
public class PhAccumulatePointsServiceImpl implements PhAccumulatePointsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhAccumulatePointsRepository phAccumulatePointsRepository;

	@Autowired
	private PhUserInfoService phUserInfoService;
	
	@Override
	public PhAccumulatePoints save(PhAccumulatePoints phAccumulatePoints){
		return phAccumulatePointsRepository.save(phAccumulatePoints);
	}
	
	@Override
	public PhAccumulatePoints findById(Long id){
		Optional<PhAccumulatePoints> optional = phAccumulatePointsRepository.findById(id);
		return optional.isPresent()?optional.get():null;
	}

	@Override
	public void delete(Long id){
		phAccumulatePointsRepository.deleteById(id);
	}

	@Override
	public List<PhAccumulatePoints> save(List<PhAccumulatePoints> entities){
		return phAccumulatePointsRepository.saveAll(entities);
	}

	@Override
	public Page<PhAccumulatePoints> finByPage(Long userId, Pageable pageable){
		return phAccumulatePointsRepository.findAll(new Specification<PhAccumulatePoints>() {
			@Override
			public Predicate toPredicate(Root<PhAccumulatePoints> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<>();
				if(null != userId){
					params.add(criteriaBuilder.equal(root.get("userId"),userId));
				}
				Predicate[] predicates = new Predicate[params.size()];
				criteriaQuery.where(params.toArray(predicates));
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime")));
				return null;
			}
		},pageable);
	}

	@Override
	public boolean countByUserIdToday(Long userId){
		int c = phAccumulatePointsRepository.countByUserIdToday(userId);
		return c > 0;
	}

	@Override
	public Map<String,Object> init(Long userId){
		Map<String,Object> resultMap = new HashMap<>();
		PhUserInfo phUserInfo = phUserInfoService.findById(userId);
		Long signCount = phUserInfo.getSignCount();
		resultMap.put("signCount",signCount);
		resultMap.put("points",phUserInfo.getPoints());
		resultMap.put("article",0L);
		resultMap.put("share",0L);
		resultMap.put("invite",0L);

		List<Object> objects = phAccumulatePointsRepository.pointsByUserIdAndToday(userId);
		for (Object object : objects) {
			Object[] obj = (Object[]) object;
			//1-阅读文章,2-分享文章,3-邀请好友完成注册
			String type = obj[0].toString();
			Long value = Long.parseLong(obj[1].toString());
			if(type.equals("1")){
				resultMap.put("article",value);
			}else if(type.equals("2")){
				resultMap.put("share",value);
			}else if(type.equals("3")){
				resultMap.put("invite",value);
			}
		}

		return resultMap;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {Exception.class, StockException.class})
	public Map<String, Object> sign(Long userId) throws Exception{
		Date now = new Date();
		Long points = 0L;
		Long signCount = 0L;//连续签到天数

		PhUserInfo phUserInfo = phUserInfoService.findById(userId);

		//查询今天是否连续签到
		Date endSignTime = phAccumulatePointsRepository.endSignTime(userId);
		if(null != endSignTime){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			int compare = DateUtils.addDays(sdf.parse(sdf.format(endSignTime)),1).compareTo(sdf.parse(sdf.format(now)));
			if(compare == 0){
				signCount = phUserInfo.getSignCount();
			}
		}


		signCount = signCount.longValue() + 1L;

		//计算积分
		switch (signCount.intValue()){
			case 1:
				points = 1L;
				break;
			case 2:
				points = 7L;
				break;
			case 3:
				points = 9L;
				break;
			case 4:
				points = 11L;
				break;
			case 5:
				points = 13L;
				break;
			default:
				points = 15L;
				break;
		}

		PhAccumulatePoints phAccumulatePoints = new PhAccumulatePoints();
		phAccumulatePoints.setCreateTime(now);
		phAccumulatePoints.setPoints(points);
		phAccumulatePoints.setPointsBalace(points);
		phAccumulatePoints.setStatus("0");
		phAccumulatePoints.setType("0");
		phAccumulatePoints.setUserId(userId);
		phAccumulatePoints.setVersion(0L);
		save(phAccumulatePoints);

		phUserInfo.setSignCount(signCount);
		phUserInfo.setPoints(phUserInfo.getPoints().longValue()+points.longValue());
		phUserInfoService.save(phUserInfo);

		Map<String,Object> resultMap = new HashMap<>();
		resultMap.put("signCount",signCount);
		resultMap.put("points",phUserInfo.getPoints());
		return resultMap;
	}
}
