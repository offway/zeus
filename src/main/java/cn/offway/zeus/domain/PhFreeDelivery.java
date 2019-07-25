package cn.offway.zeus.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * 免费送
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Entity
@Table(name = "ph_free_delivery")
public class PhFreeDelivery implements Serializable {

    /** ID **/
    private Long id;

    /** 价格 **/
    private Double price;

    /** 图片 **/
    private String image;

    /** 名称 **/
    private String name;

    /** 商品ID **/
    private Long goodsId;

    /** 件数 **/
    private Long goodsCount;

    /** 助力次数 **/
    private Long boostCount;

    /** 创建时间 **/
    private Date createTime;

    /** 备注 **/
    private String remark;

    /** 排序 **/
    private Long sort;

    /** 状态[0-初始,1-已抢光] **/
    private String status;

    /** 版本号 **/
    private Long version;

    /** 活动批次 **/
    private String batch;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "price", precision = 15, scale = 2)
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Column(name = "image", length = 100)
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Column(name = "name", length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "goods_id", length = 11)
    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    @Column(name = "goods_count", length = 11)
    public Long getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Long goodsCount) {
        this.goodsCount = goodsCount;
    }

    @Column(name = "boost_count", length = 11)
    public Long getBoostCount() {
        return boostCount;
    }

    public void setBoostCount(Long boostCount) {
        this.boostCount = boostCount;
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

    @Column(name = "sort", length = 11)
    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }

    @Column(name = "status", length = 2)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Version
    @Column(name = "version", length = 11)
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Column(name = "batch", length = 10)
    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }
}
