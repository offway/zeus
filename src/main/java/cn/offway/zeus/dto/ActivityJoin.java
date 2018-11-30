package cn.offway.zeus.dto;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * 活动参与表-每日福利
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public class ActivityJoin implements Serializable {

    /** 活动ID **/
    private Long id;

    /** 活动名称 **/
    private String name;

    /** 封面图片 **/
    private String image;

    /** 参与时间 **/
    private Date createTime;
    
    /** 截止时间 **/
    private Date endTime;

    /** 是否结束  **/
    private boolean isEnd;
    

	public ActivityJoin(Long id, String name, String image, Date createTime, Date endTime) {
		super();
		this.id = id;
		this.name = name;
		this.image = image;
		this.createTime = createTime;
		this.endTime = endTime;
		this.isEnd = endTime.before(new Date())?false:true;
	}


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


	public Date getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


	public Date getEndTime() {
		return endTime;
	}


	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


	public boolean getIsEnd() {
		return isEnd;
	}


	public void setIsEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}
}
