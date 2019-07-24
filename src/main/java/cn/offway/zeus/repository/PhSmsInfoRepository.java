package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhSmsInfo;

/**
 * 短信记录Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhSmsInfoRepository extends JpaRepository<PhSmsInfo,Long>,JpaSpecificationExecutor<PhSmsInfo> {

	@Query(nativeQuery=true,value="select count(*) from ph_sms_info where ip = ?1 and ret_code='0' and DATE_FORMAT(NOW(),'%Y-%m-%d')=DATE_FORMAT(create_time,'%Y-%m-%d')")
	int countByIp(String ip);
}
