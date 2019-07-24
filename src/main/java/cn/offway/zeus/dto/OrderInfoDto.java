package cn.offway.zeus.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import cn.offway.zeus.domain.PhAddress;
import cn.offway.zeus.domain.PhOrderExpressInfo;
import cn.offway.zeus.domain.PhOrderGoods;

public class OrderInfoDto implements Serializable{

	 /** ID **/
    private Long id;

    /** 预生成订单号 **/
    private String preorderNo;

    /** 订单号 **/
    private String orderNo;

    /** 用户ID **/
    private Long userId;

    /** 地址ID **/
    private Long addrId;

    /** 订单总价 **/
    private Double price;

    /** 实付金额 **/
    private Double amount;

    /** 店铺优惠券ID **/
    private Long mVoucherId;

    /** 店铺优惠券金额 **/
    private Double mVoucherAmount;

    /** 平台优惠券ID **/
    private Long pVoucherId;

    /** 平台优惠券金额 **/
    private Double pVoucherAmount;

    /** 钱包金额 **/
    private Double walletAmount;

    /** 运费 **/
    private Double mailFee;

    /** 状态[0-已下单,1-已付款,2-已发货,3-已收货,4-取消] **/
    private String status;

    /** 发货人 **/
    private String deliverName;

    /** 发货时间 **/
    private Date deliverTime;

    /** 留言 **/
    private String message;

    /** 创建时间 **/
    private Date createTime;

    /** 备注 **/
    private String remark;

    /** 版本号 **/
    private Long version;

    /** 商户ID **/
    private Long merchantId;

    /** 商户LOGO **/
    private String merchantLogo;

    /** 商户名称 **/
    private String merchantName;
    
    /** 支付渠道[wxpay,alipay] **/
    private String payChannel;
    
    /** 快递公司编码 **/
    private String expressCode;

    /** 快递单号 **/
    private String mailNo;
    
    /** 订单商品详情  **/
    private List<PhOrderGoods> goods;
    
    /** 地址详情  **/
    private PhOrderExpressInfo address;
    
    /** 快递详情  **/
    private Object mailContent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPreorderNo() {
		return preorderNo;
	}

	public void setPreorderNo(String preorderNo) {
		this.preorderNo = preorderNo;
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

	public Long getmVoucherId() {
		return mVoucherId;
	}

	public void setmVoucherId(Long mVoucherId) {
		this.mVoucherId = mVoucherId;
	}

	public Double getmVoucherAmount() {
		return mVoucherAmount;
	}

	public void setmVoucherAmount(Double mVoucherAmount) {
		this.mVoucherAmount = mVoucherAmount;
	}

	public Long getpVoucherId() {
		return pVoucherId;
	}

	public void setpVoucherId(Long pVoucherId) {
		this.pVoucherId = pVoucherId;
	}

	public Double getpVoucherAmount() {
		return pVoucherAmount;
	}

	public void setpVoucherAmount(Double pVoucherAmount) {
		this.pVoucherAmount = pVoucherAmount;
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

	public String getDeliverName() {
		return deliverName;
	}

	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}

	public Date getDeliverTime() {
		return deliverTime;
	}

	public void setDeliverTime(Date deliverTime) {
		this.deliverTime = deliverTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantLogo() {
		return merchantLogo;
	}

	public void setMerchantLogo(String merchantLogo) {
		this.merchantLogo = merchantLogo;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public List<PhOrderGoods> getGoods() {
		return goods;
	}

	public void setGoods(List<PhOrderGoods> goods) {
		this.goods = goods;
	}

	public PhOrderExpressInfo getAddress() {
		return address;
	}

	public void setAddress(PhOrderExpressInfo address) {
		this.address = address;
	}

	public String getExpressCode() {
		return expressCode;
	}

	public void setExpressCode(String expressCode) {
		this.expressCode = expressCode;
	}

	public String getMailNo() {
		return mailNo;
	}

	public void setMailNo(String mailNo) {
		this.mailNo = mailNo;
	}

	public Object getMailContent() {
		return mailContent;
	}

	public void setMailContent(Object mailContent) {
		this.mailContent = mailContent;
	}
	
    
}
