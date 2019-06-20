package cn.offway.zeus.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import cn.offway.zeus.domain.PhBrand;

public class MerchantInfoDto implements Serializable {

    /** ID **/
    private Long id;

    /** 商户名称 **/
    private String name;

    /** 商户LOGO **/
    private String logo;

    /** 商户地址 **/
    private String address;

    /** 发货地址ID[见ph_address.id] **/
    private Long addrId;

    /** 联系电话 **/
    private String phone;

    /** 商户邮箱 **/
    private String email;

    /** 合同编号 **/
    private String contractNo;

    /** 签约日期 **/
    private Date signDate;

    /** 到期日期 **/
    private Date expireDate;

    /** 状态[0-已申请,1-通过,2-拒绝] **/
    private String status;

    /** 创建时间 **/
    private Date createTime;

    /** 备注 **/
    private String remark;

    /** 公司 **/
    private String company;

    /** 后台登录ID[见ph_admin.id] **/
    private Long adminId;

    /** 是否包邮[0-否,1-是] **/
    private String isFreeFare;

    /**  **/
    private Double ratio;

    /** 退货地址ID[见ph_address.id] **/
    private Long returnAddrId;

    /** 商户所在城市[买手店必填] **/
    private String city;

    /** 商户类型[0-品牌商,1-买手店] **/
    private String type;

    /** 商户banner **/
    private String banner;

    /** 背景图 **/
    private String background;
    
    /** 品牌 **/
    private List<PhBrand> brands;

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

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getAddrId() {
		return addrId;
	}

	public void setAddrId(Long addrId) {
		this.addrId = addrId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Date getSignDate() {
		return signDate;
	}

	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	public String getIsFreeFare() {
		return isFreeFare;
	}

	public void setIsFreeFare(String isFreeFare) {
		this.isFreeFare = isFreeFare;
	}

	public Double getRatio() {
		return ratio;
	}

	public void setRatio(Double ratio) {
		this.ratio = ratio;
	}

	public Long getReturnAddrId() {
		return returnAddrId;
	}

	public void setReturnAddrId(Long returnAddrId) {
		this.returnAddrId = returnAddrId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public List<PhBrand> getBrands() {
		return brands;
	}

	public void setBrands(List<PhBrand> brands) {
		this.brands = brands;
	}

}
