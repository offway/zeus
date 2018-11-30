package cn.offway.zeus.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 活动参与情况
 *
 * @author wn
 * @version $v: 1.0.0, $time:2018-10-15 16:49:00 Exp $
 */
public class ProductJoin implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2260703745459269371L;

	/** 活动ID **/
    private Long id;

    /** 活动名称 **/
    private String name;


    /** 活动image **/
    private String image;


    /** 活动开始时间 **/
    private Date beginTime;

    /** 活动截止时间 **/
    private Date endTime;

    /** 创建时间 **/
    private Date createTime;
    
    /** 状态[0-进行中,1-已结束]**/
    private String status;
    
    /** 分享图片 **/
    private String shareImage;

    /** 分享标题 **/
    private String shareTitle;

    /** 分享描述 **/
    private String shareDesc;
    
    

	public ProductJoin(Long id, String image, String name, Date createTime, Date beginTime, Date endTime,String shareImage, String shareTitle, String shareDesc) {
		super();
		this.id = id;
		this.name = name;
		this.image = image;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.createTime = createTime;
		if(endTime.after(new Date())){
			this.status = "0";
		}else{
			this.status = "1";
		}
		this.shareImage = shareImage;
		this.shareTitle = shareTitle;
		this.shareDesc = shareDesc;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getShareImage() {
		return shareImage;
	}

	public void setShareImage(String shareImage) {
		this.shareImage = shareImage;
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

}
