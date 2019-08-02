package cn.offway.zeus.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * 七夕活动
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-08-02 14:52:06 Exp $
 */
@Entity
@Table(name = "ph_seventh")
public class PhSeventh implements Serializable {

    /** ID **/
    private Long id;

    /** 用户ID **/
    private Long userId;

    /** 剩余抽奖次数 **/
    private Long lotteryNum;

    /** 创建时间 **/
    private Date createTime;

    /** 版本号 **/
    private Long version;

    /** 备注 **/
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

    @Column(name = "lottery_num", length = 11)
    public Long getLotteryNum() {
        return lotteryNum;
    }

    public void setLotteryNum(Long lotteryNum) {
        this.lotteryNum = lotteryNum;
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

}
