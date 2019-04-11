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
@Table(name = "v_collect_brand")
public class VCollectBrand implements Serializable {

	/** 收藏ID **/
	private Long collectId;
	
    /** 用户ID **/
    private Long userId;

    /** ID **/
    private Long id;

    /** 品牌编码 **/
    private String code;

    /** 品牌名称 **/
    private String name;

    /** 品牌LOGO **/
    private String logo;

    /** 品牌LOGO(大) **/
    private String logoBig;

    /** 品牌banner **/
    private String banner;

    /** 类型[0-国内品牌，1-国际品牌] **/
    private String type;

    /** 简介 **/
    private String info;

    /** 背景图 **/
    private String background;

    /** 是否推荐[0-否，1-是] **/
    private String isRecommend;

    /** 商户ID **/
    private Long merchantId;

    /** 商户名称 **/
    private String merchantName;

    /** 商户LOGO **/
    private String merchantLogo;

    /** 排序 **/
    private Long sort;

    /** 创建时间 **/
    private Date createTime;

    /** 备注 **/
    private String remark;


    @Column(name = "collect_id", length = 11)
    public Long getCollectId() {
		return collectId;
	}

	public void setCollectId(Long collectId) {
		this.collectId = collectId;
	}
	
    @Column(name = "user_id", length = 11)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Id
    @Column(name = "id", length = 11)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "code", length = 100)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name", length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "logo", length = 200)
    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Column(name = "logo_big", length = 200)
    public String getLogoBig() {
        return logoBig;
    }

    public void setLogoBig(String logoBig) {
        this.logoBig = logoBig;
    }

    @Column(name = "banner", length = 100)
    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    @Column(name = "type", length = 2)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "info")
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Column(name = "background", length = 200)
    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    @Column(name = "is_recommend", length = 2)
    public String getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(String isRecommend) {
        this.isRecommend = isRecommend;
    }

    @Column(name = "merchant_id", length = 11)
    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    @Column(name = "merchant_name", length = 100)
    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    @Column(name = "merchant_logo", length = 100)
    public String getMerchantLogo() {
        return merchantLogo;
    }

    public void setMerchantLogo(String merchantLogo) {
        this.merchantLogo = merchantLogo;
    }

    @Column(name = "sort", length = 11)
    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
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
