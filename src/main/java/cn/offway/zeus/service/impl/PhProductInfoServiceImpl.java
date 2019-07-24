package cn.offway.zeus.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import cn.offway.zeus.domain.PhProductInfo;
import cn.offway.zeus.dto.ProductInfo;
import cn.offway.zeus.dto.ProductJoin;
import cn.offway.zeus.repository.PhProductInfoRepository;
import cn.offway.zeus.service.PhProductInfoService;


/**
 * 活动产品表Service接口实现
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
@Service
public class PhProductInfoServiceImpl implements PhProductInfoService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PhProductInfoRepository phProductInfoRepository;
	
	@Override
	public PhProductInfo save(PhProductInfo phProductInfo){
		return phProductInfoRepository.save(phProductInfo);
	}
	
	@Override
	public PhProductInfo getOne(Long id){
		return phProductInfoRepository.getOne(id);
	}
	
	@Override
	public Map<String, List<ProductInfo>> list(String unionid,int channel){
		Map<String, List<ProductInfo>> resultMap = new HashMap<>();
		//用户参与的活动
		List<PhProductInfo> phProductInfos = findByUnionid(unionid);
		resultMap.put("current", getProductInfos(phProductInfoRepository.findByNow(channel),phProductInfos));
		resultMap.put("next", getProductInfos(phProductInfoRepository.findBynext(channel),phProductInfos));
		resultMap.put("before", getProductInfos(phProductInfoRepository.findByBefore(channel),phProductInfos));
		return resultMap;
	}
	
	@Override
	public Page<ProductInfo> listByPage(String type,String unionid,int channel,int page, int size){
		//用户参与的活动
		List<PhProductInfo> phProductInfos = findByUnionid(unionid);
		
		List<ProductInfo> productInfos = new ArrayList<>();
		
		int total = 0;
		if("0".equals(type)){
			//进行中
			productInfos = getProductInfos(phProductInfoRepository.findByNowAndPage(channel, page*size, (page+1)*size), phProductInfos);
			total = phProductInfoRepository.countByNow(channel);
		}else if("1".equals(type)){
			//未开始
			productInfos = getProductInfos(phProductInfoRepository.findBynextAndPage(channel, page*size, (page+1)*size), phProductInfos);
			total = phProductInfoRepository.countBynext(channel);
		}else if("2".equals(type)){
			//已结束
			productInfos = getProductInfos(phProductInfoRepository.findByBeforeAndPage(channel, page*size, (page+1)*size), phProductInfos);
			total = phProductInfoRepository.countByBefore(channel);
		}
		
		return new PageImpl<ProductInfo>(productInfos, new PageRequest(page, size), total);
		
		
	}

	private List<ProductInfo> getProductInfos(List<PhProductInfo> infos,List<PhProductInfo> phProductInfos) {
		List<ProductInfo> productInfos = new ArrayList<>();
		for (PhProductInfo phProductInfo : infos) {
			ProductInfo productInfo = new ProductInfo();
			BeanUtils.copyProperties(phProductInfo, productInfo);
			for (PhProductInfo product : phProductInfos) {
				if(product.getId().longValue() == productInfo.getId().longValue()){
					productInfo.setIsJoin(true);
				}
			}
			productInfos.add(productInfo);
		}
		return productInfos;
	}
	
	@Override
	public List<PhProductInfo> findByUnionid(String unionid){
		return phProductInfoRepository.findByUnionid(unionid);
	}

	@Override
	public List<ProductJoin> findProductJoinByUnionid(String unionid,int channel) {
		return phProductInfoRepository.findProductJoinByUnionid(unionid,channel);
	}
	
	@Override
	public Page<PhProductInfo> findByType(final String type,Pageable page){
		return phProductInfoRepository.findAll(new Specification<PhProductInfo>() {
			
			@Override
			public Predicate toPredicate(Root<PhProductInfo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> params = new ArrayList<Predicate>();
				
				Date now = new Date();
				if(StringUtils.isNotBlank(type)){
					if("0".equals(type)){
						//进行中
						params.add(criteriaBuilder.lessThan(root.get("beginTime"), now));
						params.add(criteriaBuilder.isNull(root.get("video")));
						Predicate[] predicates = new Predicate[params.size()];
		                criteriaQuery.where(params.toArray(predicates));
		                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("sort")),criteriaBuilder.desc(root.get("beginTime")));
					}else if("1".equals(type)){
						//未开始
						params.add(criteriaBuilder.greaterThanOrEqualTo(root.get("beginTime"), now));
						Predicate[] predicates = new Predicate[params.size()];
		                criteriaQuery.where(params.toArray(predicates));
		                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("sort")),criteriaBuilder.asc(root.get("beginTime")));
					
					}else if("2".equals(type)){
						//已结束
						params.add(criteriaBuilder.lessThan(root.get("endTime"), now));
						params.add(criteriaBuilder.isNotNull(root.get("video")));
						Predicate[] predicates = new Predicate[params.size()];
		                criteriaQuery.where(params.toArray(predicates));
		                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("sort")),criteriaBuilder.desc(root.get("endTime")));
					}
					
				}
				
                Predicate[] predicates = new Predicate[params.size()];
                criteriaQuery.where(params.toArray(predicates));
				
				return null;
			}
		}, page);
	}
}
