package cn.offway.zeus.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * 优惠券
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public class VoucherDto implements Serializable {

    @ApiModelProperty(value ="用户ID")
    private Long userId;

    @ApiModelProperty(value ="优惠券类型[0-平台券，1-店铺券]",required = false)
    private String type;

    @ApiModelProperty(value ="商户ID",required = false)
    private Long merchantId;

    @ApiModelProperty(value ="购买总金额",required = false)
    private Double sumAmount;
    
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

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public Double getSumAmount() {
		return sumAmount;
	}

	public void setSumAmount(Double sumAmount) {
		this.sumAmount = sumAmount;
	}

}
