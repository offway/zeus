package cn.offway.zeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.offway.zeus.domain.PhStarsameImage;
import java.lang.Long;
import java.util.List;

/**
 * 明星同款banner图片Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhStarsameImageRepository extends JpaRepository<PhStarsameImage,Long>,JpaSpecificationExecutor<PhStarsameImage> {

	List<PhStarsameImage> findByStarsameIdOrderBySortAsc(Long starsameid);
}
