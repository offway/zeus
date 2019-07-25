package cn.offway.zeus.domain;



import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * VIEW
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Entity

@Table(name = "v_order_refund")
public class VOrderRefund implements Serializable {

    private String id;
    /**  **/
    private String style;

    /**  **/
    private String type;

    /**  **/
    private String merchantName;

    /**  **/
    private String status;

    /**  **/
    private String orderNo;

    /**  **/
    private Date createTime;

    /**  **/
    private Long userId;

    /** 是否整单退款[0-否,1-是] **/
    private String isComplete;

    /** 退款ID **/
    private Long refundId;

    @Id
    @Column(name = "id", length = 20)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "style", length = 6)
    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Column(name = "type", length = 2)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "merchant_name", length = 100)
    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    @Column(name = "status", length = 2)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "order_no", length = 50)
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "user_id", length = 11)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "refund_id", length = 11)
    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

    @Column(name = "is_complete", length = 2)
    public String getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(String isComplete) {
        this.isComplete = isComplete;
    }


}
