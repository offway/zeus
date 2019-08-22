package cn.offway.zeus.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 限量发售
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public class LimitedSaleInfoDto implements Serializable {

    @ApiModelProperty(value ="ID")
    private Long id;

    @ApiModelProperty(value ="名称")
    private String name;

    @ApiModelProperty(value ="封面图片")
    private String image;

    @ApiModelProperty(value ="售价")
    private String price;

    @ApiModelProperty(value ="商品ID")
    private Long goodsId;

    @ApiModelProperty(value ="开始时间")
    private Date beginTime;

    @ApiModelProperty(value ="截止时间")
    private Date endTime;

    @ApiModelProperty(value ="状态[0-未上架,1-已上架]")
    private String status;

    @ApiModelProperty(value ="创建时间")
    private Date createTime;

    @ApiModelProperty(value ="发售详情")
    private String info;

    @ApiModelProperty(value ="备注")
    private String remark;

	@ApiModelProperty(value ="目标助力次数")
	private Long boostCount;

	@ApiModelProperty(value ="当前助力次数")
	private Long currentCount;

	@ApiModelProperty(value ="市场价")
	private String originalPrice;

	@ApiModelProperty(value ="数量")
	private String saleCount;
    
    @ApiModelProperty(value ="服务器当前时间")
    private Date now;

	@ApiModelProperty(value ="单用户购买数量上线")
	private Long buyLimit;

	@ApiModelProperty(value ="当前用户已购买数量")
	private Long buyCount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


	public Date getNow() {
		return now;
	}

	public void setNow(Date now) {
		this.now = now;
	}

	public String getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(String originalPrice) {
		this.originalPrice = originalPrice;
	}

	public String getSaleCount() {
		return saleCount;
	}

	public void setSaleCount(String saleCount) {
		this.saleCount = saleCount;
	}

	public Long getBoostCount() {
		return boostCount;
	}

	public void setBoostCount(Long boostCount) {
		this.boostCount = boostCount;
	}

	public Long getCurrentCount() {
		return currentCount;
	}

	public void setCurrentCount(Long currentCount) {
		this.currentCount = currentCount;
	}

	public Long getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(Long buyCount) {
		this.buyCount = buyCount;
	}

	public Long getBuyLimit() {
		return buyLimit;
	}

	public void setBuyLimit(Long buyLimit) {
		this.buyLimit = buyLimit;
	}
}
