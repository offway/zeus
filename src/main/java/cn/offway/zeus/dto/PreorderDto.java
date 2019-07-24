package cn.offway.zeus.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import cn.offway.zeus.domain.PhAddress;

public class PreorderDto implements Serializable{

	 /** ID **/
    private Long id;

    /** 预生成订单号 **/
    private String orderNo;

    /** 用户ID **/
    private Long userId;

    /** 地址ID **/
    private Long addrId;

    /** 订单总价 **/
    private Double price;

    /** 实付金额 **/
    private Double amount;

    /** 优惠券总金额 **/
    private Double voucherAmount;

    /** 平台优惠券ID **/
    private Long pVoucherId;

    /** 钱包金额 **/
    private Double walletAmount;

    /** 总运费 **/
    private Double mailFee;

    /** 状态[0-待付款,1-已付款,2-交易关闭] **/
    private String status;

    /** 创建时间 **/
    private Date createTime;

    /** 备注 **/
    private String remark;

    /** 版本号 **/
    private Long version;
    
    /** 支付渠道[wxpay,alipay] **/
    private String payChannel;
    
    /** 订单详情 **/
    private List<OrderInfoDto> orderInfos;
    
    /** 收货地址详情  **/
    private PhAddress address;

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

	public Long getAddrId() {
		return addrId;
	}

	public void setAddrId(Long addrId) {
		this.addrId = addrId;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getVoucherAmount() {
		return voucherAmount;
	}

	public void setVoucherAmount(Double voucherAmount) {
		this.voucherAmount = voucherAmount;
	}

	public Long getpVoucherId() {
		return pVoucherId;
	}

	public void setpVoucherId(Long pVoucherId) {
		this.pVoucherId = pVoucherId;
	}

	public Double getWalletAmount() {
		return walletAmount;
	}

	public void setWalletAmount(Double walletAmount) {
		this.walletAmount = walletAmount;
	}

	public Double getMailFee() {
		return mailFee;
	}

	public void setMailFee(Double mailFee) {
		this.mailFee = mailFee;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public List<OrderInfoDto> getOrderInfos() {
		return orderInfos;
	}

	public void setOrderInfos(List<OrderInfoDto> orderInfos) {
		this.orderInfos = orderInfos;
	}

	public PhAddress getAddress() {
		return address;
	}

	public void setAddress(PhAddress address) {
		this.address = address;
	}

}
