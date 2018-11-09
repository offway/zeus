package cn.offway.zeus.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 活动产品表
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public class ProductInfo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2260703745459269371L;

	/** 活动ID **/
    private Long id;

    /** 活动名称 **/
    private String name;

    /** 活动描述 **/
    private String desc;
    
    /** 活动列表图片 **/
    private String image;

    /** 活动banner **/
    private String banner;

    /** 奖品价值[单位RMB] **/
    private Double price;

    /** 活动开始时间 **/
    private Date beginTime;

    /** 活动截止时间 **/
    private Date endTime;

    /** 创建时间 **/
    private Date createTime;

    /** 备注 **/
    private String remark;
    
    /** 是否已参加活动 **/
    private boolean isJoin = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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

	public boolean getIsJoin() {
		return isJoin;
	}

	public void setIsJoin(boolean isJoin) {
		this.isJoin = isJoin;
	}
	
	

}
