package cn.offway.zeus.dto;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

/**
 * 退款/退货
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public class RefundDto implements Serializable {

	@ApiModelProperty(value ="退款申请ID,修改申请时必传")
    private Long id;
	
    @ApiModelProperty(value ="购买订单号")
    private String orderNo;

    @ApiModelProperty(value ="用户ID")
    private Long userId;

    @ApiModelProperty(value ="类型[0-仅退款,1-退货退款,2-换货]")
    private String type;

    @ApiModelProperty(value ="退款原因")
    private String reason;

    @ApiModelProperty(value ="退款说明")
    private String content;

    @ApiModelProperty(value ="退款图片[多个,隔开]")
    private String image;
    
    @ApiModelProperty(value ="退款商品明细")
    private List<RefundGoodsDto> goods;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<RefundGoodsDto> getGoods() {
		return goods;
	}

	public void setGoods(List<RefundGoodsDto> goods) {
		this.goods = goods;
	}

}
