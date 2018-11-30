package cn.offway.zeus.dto;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * 活动表-每日福利
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public class ActivityInfo implements Serializable {

    /** 活动ID **/
    private Long id;

    /** 活动名称 **/
    private String name;

    /** 活动描述 **/
    private String productDesc;

    /** 规格 **/
    private String specification;

    /** 中奖人数 **/
    private Long winNum;

    /** 参与人数 **/
    private Long joinNum;

    /** 封面图片 **/
    private String image;

    /** 分享标题 **/
    private String shareTitle;

    /** 分享描述 **/
    private String shareDesc;

    /** 活动开始时间 **/
    private Date beginTime;

    /** 活动截止时间 **/
    private Date endTime;

    /** 状态[0-未开始,1-进行中,2-已结束，3-已过期] **/
    private String status;

    /** 创建时间 **/
    private Date createTime;

    /** 备注 **/
    private String remark;

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

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public Long getWinNum() {
		return winNum;
	}

	public void setWinNum(Long winNum) {
		this.winNum = winNum;
	}

	public Long getJoinNum() {
		return joinNum;
	}

	public void setJoinNum(Long joinNum) {
		this.joinNum = joinNum;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getShareTitle() {
		return shareTitle;
	}

	public void setShareTitle(String shareTitle) {
		this.shareTitle = shareTitle;
	}

	public String getShareDesc() {
		return shareDesc;
	}

	public void setShareDesc(String shareDesc) {
		this.shareDesc = shareDesc;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

}
