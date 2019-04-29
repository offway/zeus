package cn.offway.zeus.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * 劳动节活动奖品
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-04-04 15:18:00 Exp $
 */
@Entity
@Table(name = "ph_labor_prize")
public class PhLaborPrize implements Serializable {

    /** ID **/
    private Long id;

    /** 奖品类型[0-OFFWAY惊喜礼包,1-5~200元现金礼包] **/
    private String type;

    /** 奖项 **/
    private String name;

    /** 优惠券方案ID **/
    private Long voucherProjectId;

    /** 创建时间 **/
    private Date createTime;

    /** 状态[0-可领取,1-已领光] **/
    private String status;

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

    @Column(name = "type", length = 2)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "name", length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "voucher_project_id", length = 11)
    public Long getVoucherProjectId() {
        return voucherProjectId;
    }

    public void setVoucherProjectId(Long voucherProjectId) {
        this.voucherProjectId = voucherProjectId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "status", length = 2)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
