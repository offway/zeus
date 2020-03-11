package cn.offway.zeus.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * 明星信息表
 *
 * @author wn
 * @version $v: 1.0.0, $time:2020-03-02 13:55:02 Exp $
 */
@Entity
@Table(name = "ph_celebrity_list")
public class PhCelebrityList implements Serializable {

    /** ID **/
    private Long id;

    /** 明星名称 **/
    private String name;

    /** 明星头像 **/
    private String headimgurl;

    /** 职业 **/
    private String profession;

    /** 粉丝数量 **/
    private Long fansSum;

    /** 备注 **/
    private String remark;


    private String follow;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name", length = 200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "headimgurl", length = 500)
    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    @Column(name = "profession", length = 200)
    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    @Column(name = "fans_sum", length = 11)
    public Long getFansSum() {
        return fansSum;
    }

    public void setFansSum(Long fansSum) {
        this.fansSum = fansSum;
    }

    @Column(name = "remark", length = 200)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Transient
    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }
}
