package cn.offway.zeus.dto;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

/**
 * 下订单
 * @author wn
 *
 */
public class OrderAddDto implements Serializable{


	@ApiModelProperty(value ="地址ID")
	private Long addrId;
	
	@ApiModelProperty(value ="用户ID")
	private Long userId;
	
	@ApiModelProperty(value ="平台优惠券ID",required = false)
	private Long voucherId;
	
	@ApiModelProperty(value ="使用钱包金额",required = false)
	private Double walletAmount;
	
	@ApiModelProperty(value ="店铺详情")
	List<OrderMerchantDto> merchantDtos;

	public Long getAddrId() {
		return addrId;
	}

	public void setAddrId(Long addrId) {
		this.addrId = addrId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(Long voucherId) {
		this.voucherId = voucherId;
	}

	public Double getWalletAmount() {
		return walletAmount;
	}

	public void setWalletAmount(Double walletAmount) {
		this.walletAmount = walletAmount;
	}

	public List<OrderMerchantDto> getMerchantDtos() {
		return merchantDtos;
	}

	public void setMerchantDtos(List<OrderMerchantDto> merchantDtos) {
		this.merchantDtos = merchantDtos;
	}
	
	
	
	
	
	
}
