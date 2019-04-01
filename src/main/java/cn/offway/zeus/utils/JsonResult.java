package cn.offway.zeus.utils;

import java.io.Serializable;

public class JsonResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2508951917768066728L;

	/** 返回码 **/
	private String code = "200";
	
	/** 返回信息 **/
	private String msg;
	
	/** 返回数据 **/
	private Object data;
	
	

	public JsonResult() {
	}
	
	public JsonResult(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public JsonResult(String code, String msg, Object data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
