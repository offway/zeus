package cn.offway.zeus.dto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 订单商品
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public class OrderGoodsDto implements Serializable {

    /** ID **/
    private Long id;

    /** 预生成订单号 **/
    private String preorderNo;

    /** 订单号 **/
    private String orderNo;

    /** 价格 **/
    private Double price;

    /** 商品ID **/
    private Long goodsId;

    /** 商品名称 **/
    private String goodsName;

    /** 商品图片 **/
    private String goodsImage;

    /** 品牌ID **/
    private Long brandId;

    /** 品牌名称 **/
    private String brandName;

    /** 品牌LOGO **/
    private String brandLogo;

    /** 商品数量 **/
    private Long goodsCount;

    /** 创建时间 **/
    private Date createTime;

    /** 备注 **/
    private String remark;

    /** 商户ID **/
    private Long merchantId;

    /** 商户LOGO **/
    private String merchantLogo;

    /** 商户名称 **/
    private String merchantName;

    /** 商品库存ID **/
    private Long goodsStockId;

    /**  退款状态 **/
    private String refundStatus;


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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }

    public Long getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Long goodsCount) {
        this.goodsCount = goodsCount;
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

    public Long getGoodsStockId() {
        return goodsStockId;
    }

    public void setGoodsStockId(Long goodsStockId) {
        this.goodsStockId = goodsStockId;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }
}
