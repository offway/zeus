package cn.offway.zeus.dto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * VIEW
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public class VOrderRefundDto implements Serializable {

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

    private List<Map<String,Object>> goods;

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Map<String, Object>> getGoods() {
        return goods;
    }

    public void setGoods(List<Map<String, Object>> goods) {
        this.goods = goods;
    }

    public String getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(String isComplete) {
        this.isComplete = isComplete;
    }

    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }
}
