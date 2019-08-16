package cn.offway.zeus.domain;



import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * 品牌库
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Entity

@Table(name = "ph_brand")
public class PhBrand implements Serializable {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("品牌编码")
    private String code;

    @ApiModelProperty("品牌名称")
    private String name;

    @ApiModelProperty("品牌LOGO")
    private String logo;

    @ApiModelProperty("品牌LOGO(大)")
    private String logoBig;

    @ApiModelProperty("品牌banner")
    private String banner;

    @ApiModelProperty("类型[0-国内品牌，1-国际品牌]")
    private String type;

    @ApiModelProperty("简介")
    private String info;

    @ApiModelProperty("背景图")
    private String background;

    @ApiModelProperty("是否推荐[0-否，1-是]")
    private String isRecommend;

    @ApiModelProperty("商户ID")
    private Long merchantId;

    @ApiModelProperty("商户名称")
    private String merchantName;

    @ApiModelProperty("商户LOGO")
    private String merchantLogo;

    @ApiModelProperty("排序")
    private Long sort;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("品牌LOGO(推荐到首页)")
    private String logoIndex;

    @ApiModelProperty("品牌banner(高街潮流推荐)")
    private String bannerBig;

    @ApiModelProperty("状态[0-未上架,1-已上架]")
    private String status;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
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

    @Column(name = "logo_index", length = 200)
    public String getLogoIndex() {
        return logoIndex;
    }

    public void setLogoIndex(String logoIndex) {
        this.logoIndex = logoIndex;
    }

    @Column(name = "banner_big", length = 100)
    public String getBannerBig() {
        return bannerBig;
    }

    public void setBannerBig(String bannerBig) {
        this.bannerBig = bannerBig;
    }

    @Column(name = "status", length = 2)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
