package cn.offway.zeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.offway.zeus.domain.PhGoodsImage;

/**
 * 商品图片Repository接口
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public interface PhGoodsImageRepository extends JpaRepository<PhGoodsImage,Long>,JpaSpecificationExecutor<PhGoodsImage> {

	List<PhGoodsImage> findByGoodsId(Long goodsId);
	
	@Query(nativeQuery=true,value="select image_url from ph_goods_image where goods_id=?1 and type=?2 order by sort")
	List<String> findByGoodsId(Long goodsId,String type);
}
