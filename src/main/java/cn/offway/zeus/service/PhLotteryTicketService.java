package cn.offway.zeus.service;

import java.util.List;

import cn.offway.zeus.domain.PhLotteryTicket;
import cn.offway.zeus.domain.PhWxuserInfo;

/**
 * 抽奖券表Service接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public interface PhLotteryTicketService{

	PhLotteryTicket save(PhLotteryTicket phLotteryTicket);
	
	PhLotteryTicket getOne(Long id);
	
	int countByProductIdAndUnionidAndSource(Long productId, String unionid, String source);

	List<PhLotteryTicket> findByProductIdAndUnionid(Long productId, String unionid);

	/**
	 * 抽奖登记-发放抽奖券
	 * @param productId
	 * @param phWxuserInfo
	 * @param iphWxuserInfo
	 * @param formId
	 */
	void register(Long productId, PhWxuserInfo phWxuserInfo, PhWxuserInfo iphWxuserInfo, String formId);

	void shareTicket(String unionid, Long productId, String channel);

	int updateFormId(Long productId, String unionid, String formId);

	void appTicket(String unionid) throws Exception;

	int countByUnionidAndSource(String unionid, String source);

}
