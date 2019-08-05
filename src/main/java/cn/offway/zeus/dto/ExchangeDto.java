package cn.offway.zeus.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 换货
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
public class ExchangeDto implements Serializable {
	
    @ApiModelProperty(value ="购买订单号")
    private String orderNo;

    @ApiModelProperty(value ="用户ID")
    private Long userId;

	@ApiModelProperty(value ="换货地址ID")
	private Long addrId;

    @ApiModelProperty(value ="退款说明")
    private String content;

    @ApiModelProperty(value ="换货商品明细")
    private List<ExchangeGoodsDto> detail;

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

	public Long getAddrId() {
		return addrId;
	}

	public void setAddrId(Long addrId) {
		this.addrId = addrId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<ExchangeGoodsDto> getDetail() {
		return detail;
	}

	public void setDetail(List<ExchangeGoodsDto> detail) {
		this.detail = detail;
	}
}
