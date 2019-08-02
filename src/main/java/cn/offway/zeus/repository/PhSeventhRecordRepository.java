package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhSeventhRecord;

import java.util.List;

/**
 * 七夕活动记录Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-08-02 14:52:06 Exp $
 */
public interface PhSeventhRecordRepository extends JpaRepository<PhSeventhRecord,Long>,JpaSpecificationExecutor<PhSeventhRecord> {

    int countByShareUserIdAndCreateDateAndType(Long shareUserId,String createDate,String type);
}
