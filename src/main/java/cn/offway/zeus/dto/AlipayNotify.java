package cn.offway.zeus.dto;

import java.io.Serializable;

public class AlipayNotify implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7145721396577400310L;
	
	private String notify_time;//通知时间Date是通知的发送时间。格式为yyyy-MM-dd HH:mm:ss2015-14-27 15:45:58
	private String notify_type;//通知类型String(64)是通知的类型trade_status_sync
	private String notify_id;//通知校验IDString(128)是通知校验IDac05099524730693a8b330c5ecf72da9786
	private String app_id;//支付宝分配给开发者的应用IdString(32)是支付宝分配给开发者的应用Id2.01407E+15
	private String charset;//编码格式String(10)是编码格式，如utf-8、gbk、gb2312等utf-8
	private String version;//接口版本String(3)是调用的接口版本，固定为：1.01
	private String sign_type;//签名类型String(10)是商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2RSA2
	private String sign;//签名String(256)是请参考异步返回结果的验签601510b7970e52cc63db0f44997cf70e
	private String trade_no;//支付宝交易号String(64)是支付宝交易凭证号2.01311E+27
	private String out_trade_no;//商户订单号String(64)是原支付请求的商户订单号6.82379E+15
	private String out_biz_no;//商户业务号String(64)否商户业务ID，主要是退款通知中返回退款申请的流水号HZRF001
	private String buyer_id;//买家支付宝用户号String(16)否买家支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字2.0881E+15
	private String buyer_logon_id;//买家支付宝账号String(100)否买家支付宝账号15901825620
	private String seller_id;//卖家支付宝用户号String(30)否卖家支付宝用户号2.0881E+15
	private String seller_email;//卖家支付宝账号String(100)否卖家支付宝账号zhuzhanghu@alitest.com
	private String trade_status;//交易状态String(32)否交易目前所处的状态，见交易状态说明TRADE_CLOSED
	private String total_amount;//订单金额Number(9,2)否本次交易支付的订单金额，单位为人民币（元）20
	private String receipt_amount;//实收金额Number(9,2)否商家在交易中实际收到的款项，单位为元15
	private String invoice_amount;//开票金额Number(9,2)否用户在交易中支付的可开发票的金额10
	private String buyer_pay_amount;//付款金额Number(9,2)否用户在交易中支付的金额13.88
	private String point_amount;//集分宝金额Number(9,2)否使用集分宝支付的金额12
	private String refund_fee;//总退款金额Number(9,2)否退款通知中，返回总退款金额，单位为元，支持两位小数2.58
	private String subject;//订单标题String(256)否商品的标题/交易标题/订单标题/订单关键字等，是请求时对应的参数，原样通知回来当面付交易
	private String body;//商品描述String(400)否该订单的备注、描述、明细等。对应请求时的body参数，原样通知回来当面付交易内容
	private String gmt_create;//交易创建时间Date否该笔交易创建的时间。格式为yyyy-MM-dd HH:mm:ss42121.65691
	private String gmt_payment;//交易付款时间Date否该笔交易的买家付款时间。格式为yyyy-MM-dd HH:mm:ss42121.65691
	private String gmt_refund;//交易退款时间Date否该笔交易的退款时间。格式为yyyy-MM-dd HH:mm:ss.S42122.65691
	private String gmt_close;//交易结束时间Date否该笔交易结束时间。格式为yyyy-MM-dd HH:mm:ss42123.65691
	private String fund_bill_list;//支付金额信息String(512)否支付成功的各个渠道金额信息，详见资金明细信息说明[{“amount”:“15.00”,“fundChannel”:“ALIPAYACCOUNT”}]
	private String passback_params;//回传参数String(512)否公共回传参数，如果请求时传递了该参数，则返回给商户时会在异步通知时将该参数原样返回。本参数必须进行UrlEncode之后才可以发送给支付宝merchantBizType%3d3C%26merchantBizNo%3d2016010101111
	private String voucher_detail_list;//优惠券信息String否本交易支付时所使用的所有优惠券信息，详见优惠券信息说明[{“amount”:“0.20”,“merchantContribute”:“0.00”,“name”:“一键创建券模板的券名称”,“otherContribute”:“0.20”,“type”:“ALIPAY_DISCOUNT_VOUCHER”,“memo”:“学生卡8折优惠”]
	
	public String getNotify_time() {
		return notify_time;
	}
	public void setNotify_time(String notify_time) {
		this.notify_time = notify_time;
	}
	public String getNotify_type() {
		return notify_type;
	}
	public void setNotify_type(String notify_type) {
		this.notify_type = notify_type;
	}
	public String getNotify_id() {
		return notify_id;
	}
	public void setNotify_id(String notify_id) {
		this.notify_id = notify_id;
	}
	public String getApp_id() {
		return app_id;
	}
	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getSign_type() {
		return sign_type;
	}
	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getTrade_no() {
		return trade_no;
	}
	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getOut_biz_no() {
		return out_biz_no;
	}
	public void setOut_biz_no(String out_biz_no) {
		this.out_biz_no = out_biz_no;
	}
	public String getBuyer_id() {
		return buyer_id;
	}
	public void setBuyer_id(String buyer_id) {
		this.buyer_id = buyer_id;
	}
	public String getBuyer_logon_id() {
		return buyer_logon_id;
	}
	public void setBuyer_logon_id(String buyer_logon_id) {
		this.buyer_logon_id = buyer_logon_id;
	}
	public String getSeller_id() {
		return seller_id;
	}
	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}
	public String getSeller_email() {
		return seller_email;
	}
	public void setSeller_email(String seller_email) {
		this.seller_email = seller_email;
	}
	public String getTrade_status() {
		return trade_status;
	}
	public void setTrade_status(String trade_status) {
		this.trade_status = trade_status;
	}
	public String getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}
	public String getReceipt_amount() {
		return receipt_amount;
	}
	public void setReceipt_amount(String receipt_amount) {
		this.receipt_amount = receipt_amount;
	}
	public String getInvoice_amount() {
		return invoice_amount;
	}
	public void setInvoice_amount(String invoice_amount) {
		this.invoice_amount = invoice_amount;
	}
	public String getBuyer_pay_amount() {
		return buyer_pay_amount;
	}
	public void setBuyer_pay_amount(String buyer_pay_amount) {
		this.buyer_pay_amount = buyer_pay_amount;
	}
	public String getPoint_amount() {
		return point_amount;
	}
	public void setPoint_amount(String point_amount) {
		this.point_amount = point_amount;
	}
	public String getRefund_fee() {
		return refund_fee;
	}
	public void setRefund_fee(String refund_fee) {
		this.refund_fee = refund_fee;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getGmt_create() {
		return gmt_create;
	}
	public void setGmt_create(String gmt_create) {
		this.gmt_create = gmt_create;
	}
	public String getGmt_payment() {
		return gmt_payment;
	}
	public void setGmt_payment(String gmt_payment) {
		this.gmt_payment = gmt_payment;
	}
	public String getGmt_refund() {
		return gmt_refund;
	}
	public void setGmt_refund(String gmt_refund) {
		this.gmt_refund = gmt_refund;
	}
	public String getGmt_close() {
		return gmt_close;
	}
	public void setGmt_close(String gmt_close) {
		this.gmt_close = gmt_close;
	}
	public String getFund_bill_list() {
		return fund_bill_list;
	}
	public void setFund_bill_list(String fund_bill_list) {
		this.fund_bill_list = fund_bill_list;
	}
	public String getPassback_params() {
		return passback_params;
	}
	public void setPassback_params(String passback_params) {
		this.passback_params = passback_params;
	}
	public String getVoucher_detail_list() {
		return voucher_detail_list;
	}
	public void setVoucher_detail_list(String voucher_detail_list) {
		this.voucher_detail_list = voucher_detail_list;
	}

	
}
