package cn.offway.zeus.dto;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class OrderMerchantDto implements Serializable{

	
	@ApiModelProperty(value ="店铺ID")
	private Long merchantId;
	
	@ApiModelProperty(value ="店铺优惠券ID",required = false)
	private Long voucherId;
	
	@ApiModelProperty(value ="购买详情")
	private List<OrderInitStockDto> stocks;
	
	@ApiModelProperty(value ="留言",required = false)
	private String message;

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public Long getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(Long voucherId) {
		this.voucherId = voucherId;
	}

	public List<OrderInitStockDto> getStocks() {
		return stocks;
	}

	public void setStocks(List<OrderInitStockDto> stocks) {
		this.stocks = stocks;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
