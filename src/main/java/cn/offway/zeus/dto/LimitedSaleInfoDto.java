package cn.offway.zeus.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 限量发售
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public class LimitedSaleInfoDto implements Serializable {

    /** ID **/
    private Long id;

    /** 名称 **/
    private String name;

    /** 封面图片 **/
    private String image;

    /** 售价 **/
    private Double price;

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
    
    /** 是否助力 **/
    private boolean assisted;
    
    /** 服务器当前时间 **/
    private Date now;

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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean isAssisted() {
		return assisted;
	}

	public void setAssisted(boolean assisted) {
		this.assisted = assisted;
	}

	public Date getNow() {
		return now;
	}

	public void setNow(Date now) {
		this.now = now;
	}
	
}
