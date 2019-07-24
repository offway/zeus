package cn.offway.zeus.dto;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class OrderInitDto implements Serializable{

	@ApiModelProperty(value ="地址ID")
	private Long addrId;
	
	@ApiModelProperty(value ="用户ID")
	private Long userId;
	
	@ApiModelProperty(value ="购买详情")
	private List<OrderInitStockDto> stocks;
	
	public Long getAddrId() {
		return addrId;
	}

	public void setAddrId(Long addrId) {
		this.addrId = addrId;
	}

	public List<OrderInitStockDto> getStocks() {
		return stocks;
	}

	public void setStocks(List<OrderInitStockDto> stocks) {
		this.stocks = stocks;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
}
