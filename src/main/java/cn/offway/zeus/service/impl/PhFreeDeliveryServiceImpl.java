package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.offway.zeus.service.PhFreeDeliveryBoostService;
import cn.offway.zeus.service.PhFreeDeliveryService;
import cn.offway.zeus.service.PhFreeDeliveryUserService;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.domain.PhFreeDelivery;
import cn.offway.zeus.domain.PhFreeDeliveryBoost;
import cn.offway.zeus.domain.PhFreeDeliveryUser;
import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.domain.VPickGoods;
import cn.offway.zeus.exception.StockException;
import cn.offway.zeus.repository.PhFreeDeliveryRepository;


/**
 * 免费送Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Service
public class PhFreeDeliveryServiceImpl implements PhFreeDeliveryService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhFreeDeliveryRepository phFreeDeliveryRepository;
	
	@Autowired
	private PhFreeDeliveryUserService phFreeDeliveryUserService;
	
	@Autowired
	private PhUserInfoService phUserInfoService;
	
	@Autowired
	private PhFreeDeliveryBoostService phFreeDeliveryBoostService;

	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Override
	public PhFreeDelivery save(PhFreeDelivery phFreeDelivery){
		return phFreeDeliveryRepository.save(phFreeDelivery);
	}
	
	@Override
	public PhFreeDelivery getOne(Long id){
		return phFreeDeliveryRepository.getOne(id);
	}
	
	@Override
	public Page<PhFreeDelivery> findByPage(Pageable page){
		return phFreeDeliveryRepository.findAll(new Specification<PhFreeDelivery>() {
			
			@Override
			public Predicate toPredicate(Root<PhFreeDelivery> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				
                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get("status")),criteriaBuilder.asc(root.get("sort")));
				return null;
			}
		}, page);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {Exception.class,StockException.class})
	public JsonResult boost(Long freeDeliveryId, Long userId, Long boostUserId) throws Exception{
		
		Date now = new Date();
		PhFreeDelivery phFreeDelivery = getOne(freeDeliveryId);
		if("0".equals(phFreeDelivery.getStatus())){
			PhFreeDeliveryUser phFreeDeliveryUser = phFreeDeliveryUserService.findByFreeDeliveryIdAndUserId(freeDeliveryId, userId);
			if(null == phFreeDeliveryUser){
				
				PhUserInfo phUserInfo = phUserInfoService.getOne(userId);
				phFreeDeliveryUser = new PhFreeDeliveryUser();
				phFreeDeliveryUser.setBoostCount(phFreeDelivery.getBoostCount());
				phFreeDeliveryUser.setCreateTime(now);
				phFreeDeliveryUser.setCurrentCount(0L);
				phFreeDeliveryUser.setFreeDeliveryId(freeDeliveryId);
				phFreeDeliveryUser.setHeadimgurl(phUserInfo.getHeadimgurl());
				phFreeDeliveryUser.setNickname(phUserInfo.getNickname());
				phFreeDeliveryUser.setUserId(userId);
				phFreeDeliveryUser.setVersion(0L);
			}else{
				int count = phFreeDeliveryBoostService.countByFreeDeliveryUserIdAndAndBoostUserId(phFreeDeliveryUser.getId(),boostUserId);
				if(count>0){
					//不能重复助力
					return jsonResultHelper.buildFailJsonResult(CommonResultCode.FREE_BOOSTED);
				}
			}



			if(phFreeDeliveryUser.getCurrentCount()+1L > phFreeDelivery.getBoostCount().intValue()){
				return jsonResultHelper.buildFailJsonResult(CommonResultCode.FREE_LIMIT);
			}

			phFreeDeliveryUser.setCurrentCount(phFreeDeliveryUser.getCurrentCount()+1L);

			phFreeDeliveryUser.setLastTime(now);
			phFreeDeliveryUser = phFreeDeliveryUserService.save(phFreeDeliveryUser);

			//查询是否该商品是否已抢光
			int giveCount = phFreeDeliveryUserService.countByFreeDeliveryIdAndCurrentCount(freeDeliveryId,phFreeDelivery.getBoostCount());
			if(giveCount == phFreeDelivery.getGoodsCount().intValue()){
				phFreeDelivery.setStatus("1");//已抢光
				save(phFreeDelivery);
			}

			PhUserInfo boostUser = phUserInfoService.getOne(boostUserId);
			
			PhFreeDeliveryBoost phFreeDeliveryBoost = new PhFreeDeliveryBoost();
			phFreeDeliveryBoost.setBoostHeadimgurl(boostUser.getHeadimgurl());
			phFreeDeliveryBoost.setBoostNickname(boostUser.getNickname());
			phFreeDeliveryBoost.setBoostUserId(boostUserId);
			phFreeDeliveryBoost.setCreateTime(now);
			phFreeDeliveryBoost.setFreeDeliveryUserId(phFreeDeliveryUser.getId());
			phFreeDeliveryBoostService.save(phFreeDeliveryBoost);
		}
		return jsonResultHelper.buildSuccessJsonResult(null);
	}
}
