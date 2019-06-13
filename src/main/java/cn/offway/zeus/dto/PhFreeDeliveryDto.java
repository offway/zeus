package cn.offway.zeus.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 免费送
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
public class PhFreeDeliveryDto implements Serializable {

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

    /** 助力进度 **/
    private Long currentCount;
    
    /** 助力榜单 **/
    private List<Map<String, Object>> boosts;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public Long getGoodsCount() {
		return goodsCount;
	}

	public void setGoodsCount(Long goodsCount) {
		this.goodsCount = goodsCount;
	}

	public Long getBoostCount() {
		return boostCount;
	}

	public void setBoostCount(Long boostCount) {
		this.boostCount = boostCount;
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

	public Long getSort() {
		return sort;
	}

	public void setSort(Long sort) {
		this.sort = sort;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getCurrentCount() {
		return currentCount;
	}

	public void setCurrentCount(Long currentCount) {
		this.currentCount = currentCount;
	}

	public List<Map<String, Object>> getBoosts() {
		return boosts;
	}

	public void setBoosts(List<Map<String, Object>> boosts) {
		this.boosts = boosts;
	}

}
