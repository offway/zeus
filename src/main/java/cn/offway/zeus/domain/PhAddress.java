package cn.offway.zeus.domain;

import java.io.Serializable;
import javax.persistence.*;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 地址管理
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-01 11:26:00 Exp $
 */
@Entity
@Table(name = "ph_address")
public class PhAddress implements Serializable {

    @ApiModelProperty(value ="ID")
    private Long id;

    @ApiModelProperty(value ="用户ID")
    private Long userId;

    @ApiModelProperty(value ="姓名")
    private String realName;

    @ApiModelProperty(value ="手机号")
    private String phone;

    @ApiModelProperty(value ="省份")
    private String province;

    @ApiModelProperty(value ="城市")
    private String city;

    @ApiModelProperty(value ="区/县")
    private String county;

    @ApiModelProperty(value ="详细地址")
    private String content;

    @ApiModelProperty(value ="是否默认[0-否,1-是]")
    private String isDefault;

    @ApiModelProperty(value ="创建时间")
    private Date createTime;

    @ApiModelProperty(value ="备注")
    private String remark;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "user_id", length = 11)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "real_name", length = 50)
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Column(name = "phone", length = 20)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "province", length = 20)
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Column(name = "city", length = 20)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "county", length = 20)
    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    @Column(name = "content", length = 200)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "is_default", length = 2)
    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
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
