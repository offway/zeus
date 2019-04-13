package cn.offway.zeus.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class OrderInitStockDto implements Serializable {

	@ApiModelProperty(value = "商品库存ID")
	private Long stockId;

	@ApiModelProperty(value = "商品数量")
	private Long num;

	public Long getStockId() {
		return stockId;
	}

	public void setStockId(Long stockId) {
		this.stockId = stockId;
	}

	public Long getNum() {
		return num;
	}

	public void setNum(Long num) {
		this.num = num;
	}

}
