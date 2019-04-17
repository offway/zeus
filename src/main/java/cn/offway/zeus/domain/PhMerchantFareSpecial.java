package cn.offway.zeus.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * 商户运费特殊表
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Entity
@Table(name = "ph_merchant_fare_special")
public class PhMerchantFareSpecial implements Serializable {

    /** ID **/
    private Long id;

    /** 商户运费模板ID **/
    private Long merchantFareId;

    /** 省份 **/
    private String province;

    /** 城市 **/
    private String city;

    /** 区/县 **/
    private String county;

    /** 运费首件数 **/
    private Long fareFirstNum;

    /** 运费首费[单位：元] **/
    private Double fareFirstPrice;

    /** 运费续件数 **/
    private Long fareNextNum;

    /** 运费续费[单位：元] **/
    private Double fareNextPrice;

    /** 创建时间 **/
    private Date createTime;

    /** 备注 **/
    private String remark;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "merchant_fare_id", length = 11)
    public Long getMerchantFareId() {
        return merchantFareId;
    }

    public void setMerchantFareId(Long merchantFareId) {
        this.merchantFareId = merchantFareId;
    }

    @Column(name = "province", length = 20)
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Column(name = "city", length = 20)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "county", length = 20)
    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    @Column(name = "fare_first_num", length = 11)
    public Long getFareFirstNum() {
        return fareFirstNum;
    }

    public void setFareFirstNum(Long fareFirstNum) {
        this.fareFirstNum = fareFirstNum;
    }

    @Column(name = "fare_first_price", precision = 15, scale = 2)
    public Double getFareFirstPrice() {
        return fareFirstPrice;
    }

    public void setFareFirstPrice(Double fareFirstPrice) {
        this.fareFirstPrice = fareFirstPrice;
    }

    @Column(name = "fare_next_num", length = 11)
    public Long getFareNextNum() {
        return fareNextNum;
    }

    public void setFareNextNum(Long fareNextNum) {
        this.fareNextNum = fareNextNum;
    }

    @Column(name = "fare_next_price", precision = 15, scale = 2)
    public Double getFareNextPrice() {
        return fareNextPrice;
    }

    public void setFareNextPrice(Double fareNextPrice) {
        this.fareNextPrice = fareNextPrice;
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

}
