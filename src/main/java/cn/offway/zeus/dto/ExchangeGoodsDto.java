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
public class ExchangeGoodsDto implements Serializable {


	@ApiModelProperty(value ="原库存ID")
	private Long sourceStockId;

	@ApiModelProperty(value ="目标库存ID")
	private Long targetStockId;

    @ApiModelProperty(value ="换货原因")
    private String reason;

    @ApiModelProperty(value ="换货的尺码颜色")
    private String remark;

	public Long getSourceStockId() {
		return sourceStockId;
	}

	public void setSourceStockId(Long sourceStockId) {
		this.sourceStockId = sourceStockId;
	}

	public Long getTargetStockId() {
		return targetStockId;
	}

	public void setTargetStockId(Long targetStockId) {
		this.targetStockId = targetStockId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
