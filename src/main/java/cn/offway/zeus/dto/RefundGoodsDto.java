package cn.offway.zeus.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * 退款/退货商品明细
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public class RefundGoodsDto implements Serializable {

	@ApiModelProperty(value ="订单商品ID")
    private Long orderGoodsId;
	
    @ApiModelProperty(value ="退款商品数量")
    private Long goodsCount;

	public Long getOrderGoodsId() {
		return orderGoodsId;
	}

	public void setOrderGoodsId(Long orderGoodsId) {
		this.orderGoodsId = orderGoodsId;
	}

	public Long getGoodsCount() {
		return goodsCount;
	}

	public void setGoodsCount(Long goodsCount) {
		this.goodsCount = goodsCount;
	}
    
}
