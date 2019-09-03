package cn.offway.zeus.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import io.swagger.annotations.ApiModelProperty;


/**
 * 用户信息
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Entity

@Table(name = "ph_user_info")
public class PhUserInfo implements Serializable {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("手机")
    private String phone;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("用户的性别，值为1时是男性，值为2时是女性，值为0时是未知")
    private String sex;

    //@DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty("生日")
    private Date birthday;

    @ApiModelProperty("身高[单位:cm]")
    private Long height;

    @ApiModelProperty("体重")
    private Double weight;

    @ApiModelProperty("用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。")
    private String headimgurl;

    @ApiModelProperty("只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。")
    private String unionid;
    
    @ApiModelProperty("微博ID")
    private String weiboid;
    
    @ApiModelProperty("QQID")
    private String qqid;
    
    @ApiModelProperty("余额")
    private Double balance;
    
    @ApiModelProperty("收藏数量")
    private Long collectCount;
    
    @ApiModelProperty("优惠券数量")
    private Long voucherCount;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("版本号")
    private Long version;

    @ApiModelProperty("备注")
    private String remark;
    
    @ApiModelProperty("是否赚钱达人[0-否，1-是] ")
    private String isMm;
    
    @ApiModelProperty("渠道[HY-欢阅传媒]")
    private String channel;

    @ApiModelProperty("支付宝用户ID")
    private String alipayUserId;

    @ApiModelProperty("支付宝用户昵称")
    private String alipayNickName;

    @ApiModelProperty("积分")
    private Long points;

    @ApiModelProperty("连续积分签到天数")
    private Long signCount;



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "phone", length = 11)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "nickname", length = 200)
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Column(name = "sex", length = 2)
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "birthday")
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Column(name = "height", length = 11)
    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    @Column(name = "weight", precision = 15, scale = 2)
    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Column(name = "headimgurl", length = 500)
    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    @Column(name = "unionid", length = 200)
    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
    
    @Column(name = "weiboid", length = 200)
    public String getWeiboid() {
		return weiboid;
	}

	public void setWeiboid(String weiboid) {
		this.weiboid = weiboid;
	}

    @Column(name = "qqid", length = 200)
	public String getQqid() {
		return qqid;
	}

	public void setQqid(String qqid) {
		this.qqid = qqid;
	}

	@Column(name = "balance", precision = 15, scale = 2)
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Column(name = "collect_count", length = 11)
    public Long getCollectCount() {
		return collectCount;
	}

	public void setCollectCount(Long collectCount) {
		this.collectCount = collectCount;
	}

	@Column(name = "voucher_count", length = 11)
	public Long getVoucherCount() {
		return voucherCount;
	}

	public void setVoucherCount(Long voucherCount) {
		this.voucherCount = voucherCount;
	}

	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Version
    @Column(name = "version", length = 11)
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Column(name = "remark", length = 200)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    @Column(name = "is_mm", length = 2)
    public String getIsMm() {
        return isMm;
    }

    public void setIsMm(String isMm) {
        this.isMm = isMm;
    }

    @Column(name = "channel", length = 20)
	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

    @Column(name = "alipay_user_id", length = 50)
    public String getAlipayUserId() {
        return alipayUserId;
    }

    public void setAlipayUserId(String alipayUserId) {
        this.alipayUserId = alipayUserId;
    }

    @Column(name = "alipay_nick_name", length = 100)
    public String getAlipayNickName() {
        return alipayNickName;
    }

    public void setAlipayNickName(String alipayNickName) {
        this.alipayNickName = alipayNickName;
    }

    @Column(name = "points", length = 11)
    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    @Column(name = "sign_count", length = 11)
    public Long getSignCount() {
        return signCount;
    }

    public void setSignCount(Long signCount) {
        this.signCount = signCount;
    }
}
