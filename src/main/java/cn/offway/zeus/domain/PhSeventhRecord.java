package cn.offway.zeus.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * 七夕活动记录
 *
 * @author wn
 * @version $v: 1.0.0, $time:2019-08-02 14:52:06 Exp $
 */
@Entity
@Table(name = "ph_seventh_record")
public class PhSeventhRecord implements Serializable {

    /** ID **/
    private Long id;

    /** 类型[0-分享,1-领取] **/
    private String type;

    /** 分享用户ID **/
    private Long shareUserId;

    /** 领取用户ID **/
    private Long receiveUserId;

    /** 日期[yyyy-MM-dd] **/
    private String createDate;

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

    @Column(name = "share_user_id", length = 11)
    public Long getShareUserId() {
        return shareUserId;
    }

    public void setShareUserId(Long shareUserId) {
        this.shareUserId = shareUserId;
    }

    @Column(name = "receive_user_id", length = 11)
    public Long getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(Long receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    @Column(name = "create_date", length = 20)
    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

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
