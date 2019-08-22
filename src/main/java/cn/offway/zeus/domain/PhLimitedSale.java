package cn.offway.zeus.domain;



import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * 限量发售
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Entity

@Table(name = "ph_limited_sale")
public class PhLimitedSale implements Serializable {

    /** ID **/
    private Long id;

    /** 名称 **/
    private String name;

    /** 封面图片 **/
    private String image;

    /** 售价 **/
    private String price;

    /** 商品ID **/
    private Long goodsId;

    /** 开始时间 **/
    private Date beginTime;

    /** 截止时间 **/
    private Date endTime;

    /** 状态[0-未上架,1-已上架] **/
    private String status;

    /** 创建时间 **/
    private Date createTime;

    /** 发售详情 **/
    private String info;

    /** 备注 **/
    private String remark;

    /** 市场价 **/
    private String originalPrice;

    /** 数量 **/
    private String saleCount;

    /** 目标助力次数 **/
    private Long boostCount;

    /** 平台 **/
    private Long channel;

    /** 用户类型[0-新用户,1-新老用户] **/
    private String userType;

    /** 单用户购买数量上限 **/
    private Long buyLimit;



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name", length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "image", length = 100)
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Column(name = "price", length = 50)
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Column(name = "goods_id", length = 11)
    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "begin_time")
    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Column(name = "status", length = 2)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "info")
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Column(name = "remark", length = 200)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "original_price", length = 50)
    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    @Column(name = "sale_count", length = 50)
    public String getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(String saleCount) {
        this.saleCount = saleCount;
    }

    @Column(name = "boost_count", length = 11)
    public Long getBoostCount() {
        return boostCount;
    }

    public void setBoostCount(Long boostCount) {
        this.boostCount = boostCount;
    }

    @Column(name = "channel", length = 8)
    public Long getChannel() {
        return channel;
    }

    public void setChannel(Long channel) {
        this.channel = channel;
    }

    @Column(name = "user_type", length = 2)
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Column(name = "buy_limit", length = 11)
    public Long getBuyLimit() {
        return buyLimit;
    }

    public void setBuyLimit(Long buyLimit) {
        this.buyLimit = buyLimit;
    }
}
