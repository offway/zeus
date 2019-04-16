package cn.offway.zeus.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * 预生成订单店铺
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Entity
@Table(name = "ph_preorder_merchant")
public class PhPreorderMerchant implements Serializable {

    /** ID **/
    private Long id;

    /** 预生成订单号 **/
    private String orderNo;

    /** 用户ID **/
    private Long userId;

    /** 商户ID **/
    private Long merchantId;

    /** 商户LOGO **/
    private String merchantLogo;

    /** 商户名称 **/
    private String merchantName;

    /** 店铺优惠券ID **/
    private Long mVoucherId;

    /** 店铺优惠券金额 **/
    private Double mVoucherAmount;

    /** 运费 **/
    private Double mailFee;

    /** 留言 **/
    private String message;

    /** 创建时间 **/
    private Date createTime;

    /** 备注 **/
    private String remark;

    /** 钱包金额 **/
    private Double walletAmount;

    /** 平台优惠券ID **/
    private Long pVoucherId;

    /** 平台优惠券金额 **/
    private Double pVoucherAmount;

    /** 订单总价 **/
    private Double price;

    /** 实付金额 **/
    private Double amount;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "order_no", length = 50)
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Column(name = "user_id", length = 11)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "merchant_id", length = 11)
    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    @Column(name = "merchant_logo", length = 100)
    public String getMerchantLogo() {
        return merchantLogo;
    }

    public void setMerchantLogo(String merchantLogo) {
        this.merchantLogo = merchantLogo;
    }

    @Column(name = "merchant_name", length = 100)
    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    @Column(name = "m_voucher_id", length = 11)
    public Long getMVoucherId() {
        return mVoucherId;
    }

    public void setMVoucherId(Long mVoucherId) {
        this.mVoucherId = mVoucherId;
    }

    @Column(name = "m_voucher_amount", precision = 15, scale = 2)
    public Double getMVoucherAmount() {
        return mVoucherAmount;
    }

    public void setMVoucherAmount(Double mVoucherAmount) {
        this.mVoucherAmount = mVoucherAmount;
    }

    @Column(name = "mail_fee", precision = 15, scale = 2)
    public Double getMailFee() {
        return mailFee;
    }

    public void setMailFee(Double mailFee) {
        this.mailFee = mailFee;
    }

    @Column(name = "message", length = 200)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "remark", length = 200)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "wallet_amount", precision = 15, scale = 2)
    public Double getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(Double walletAmount) {
        this.walletAmount = walletAmount;
    }

    @Column(name = "p_voucher_id", length = 11)
    public Long getPVoucherId() {
        return pVoucherId;
    }

    public void setPVoucherId(Long pVoucherId) {
        this.pVoucherId = pVoucherId;
    }

    @Column(name = "p_voucher_amount", precision = 15, scale = 2)
    public Double getPVoucherAmount() {
        return pVoucherAmount;
    }

    public void setPVoucherAmount(Double pVoucherAmount) {
        this.pVoucherAmount = pVoucherAmount;
    }

    @Column(name = "price", precision = 15, scale = 2)
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Column(name = "amount", precision = 15, scale = 2)
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

}
