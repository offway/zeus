package cn.offway.zeus.dto;

import java.io.Serializable;
import javax.persistence.*;

import cn.offway.zeus.domain.PhGoods;

import java.util.Date;
import java.util.List;

/**
 * 品牌库
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public class BrandRecommendDto implements Serializable {

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

    /** 排序 **/
    private Long sort;

    /** 创建时间 **/
    private Date createTime;

    /** 备注 **/
    private String remark;
    
    /** 品牌LOGO(推荐到首页) **/
    private String logoIndex;

    /** 品牌banner(高街潮流推荐) **/
    private String bannerBig;
    
    private List<PhGoods> goods;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getLogoBig() {
		return logoBig;
	}

	public void setLogoBig(String logoBig) {
		this.logoBig = logoBig;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getIsRecommend() {
		return isRecommend;
	}

	public void setIsRecommend(String isRecommend) {
		this.isRecommend = isRecommend;
	}

	public Long getSort() {
		return sort;
	}

	public void setSort(Long sort) {
		this.sort = sort;
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

	public List<PhGoods> getGoods() {
		return goods;
	}

	public void setGoods(List<PhGoods> goods) {
		this.goods = goods;
	}

	public String getLogoIndex() {
		return logoIndex;
	}

	public void setLogoIndex(String logoIndex) {
		this.logoIndex = logoIndex;
	}

	public String getBannerBig() {
		return bannerBig;
	}

	public void setBannerBig(String bannerBig) {
		this.bannerBig = bannerBig;
	}
   
}
