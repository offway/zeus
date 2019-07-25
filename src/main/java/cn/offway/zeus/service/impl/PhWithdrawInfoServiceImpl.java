package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import cn.offway.zeus.domain.PhCapitalFlow;
import cn.offway.zeus.domain.PhUserInfo;
import cn.offway.zeus.service.PhOrderInfoService;
import cn.offway.zeus.service.PhUserInfoService;
import cn.offway.zeus.utils.CommonResultCode;
import cn.offway.zeus.utils.JsonResult;
import cn.offway.zeus.utils.JsonResultHelper;
import cn.offway.zeus.utils.MathUtils;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import cn.offway.zeus.service.PhWithdrawInfoService;

import cn.offway.zeus.domain.PhWithdrawInfo;
import cn.offway.zeus.repository.PhWithdrawInfoRepository;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


/**
 * 提现订单Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-07-25 13:18:47 Exp $
 */
@Service
public class PhWithdrawInfoServiceImpl implements PhWithdrawInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhWithdrawInfoRepository phWithdrawInfoRepository;

	@Autowired
	private PhUserInfoService phUserInfoService;

	@Autowired
	private PhOrderInfoService phOrderInfoService;

	@Autowired
	private JsonResultHelper jsonResultHelper;
	
	@Override
	public PhWithdrawInfo save(PhWithdrawInfo phWithdrawInfo){
		return phWithdrawInfoRepository.save(phWithdrawInfo);
	}
	
	@Override
	public PhWithdrawInfo findById(Long id){
		Optional<PhWithdrawInfo> optional = phWithdrawInfoRepository.findById(id);
		return optional.isPresent()?optional.get():null;
	}

	@Override
	public void delete(Long id){
		phWithdrawInfoRepository.deleteById(id);
	}

	@Override
	public List<PhWithdrawInfo> save(List<PhWithdrawInfo> entities){
		return phWithdrawInfoRepository.saveAll(entities);
	}

	@Override
	public Page<PhWithdrawInfo> findByPage(Long userId, Pageable page){
		return phWithdrawInfoRepository.findAll(new Specification<PhWithdrawInfo>() {

			@Override
			public Predicate toPredicate(Root<PhWithdrawInfo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();

				Predicate[] predicates = new Predicate[params.size()];
				if(null != userId){
					params.add(criteriaBuilder.equal(root.get("userId"), userId));
				}
				criteriaQuery.where(params.toArray(predicates));
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime")));
				return null;
			}
		}, page);
	}
	@Override
	public JsonResult withdraw(Long userId, double amount){
		PhUserInfo phUserInfo = phUserInfoService.findById(userId);
		double balance = phUserInfo.getBalance();
		balance = MathUtils.sub(balance,amount);
		if (balance < 0){
			//余额不足
			return jsonResultHelper.buildFailJsonResult(CommonResultCode.BALANCE_LESS);
		}
		PhWithdrawInfo phWithdrawInfo = new PhWithdrawInfo();
		phWithdrawInfo.setAmount(amount);
		phWithdrawInfo.setCreateTime(new Date());
		phWithdrawInfo.setOrderNo(phOrderInfoService.generateOrderNo("12"));
		phWithdrawInfo.setStatus("0");
		phWithdrawInfo.setUserId(userId);
        phWithdrawInfo.setAlipayUserId(phUserInfo.getAlipayUserId());
		phWithdrawInfo.setVersion(0L);
		save(phWithdrawInfo);
		return jsonResultHelper.buildSuccessJsonResult(null);
	}
}
