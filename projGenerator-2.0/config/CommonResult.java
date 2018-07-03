package com.ld.model;

/**
 * 所有工程通用的数据结构
 * 
 * @version 2.0
 * @since 2017-11-03
 * @author lmkang25@163.com
 *
 */
public class CommonResult {

	private Integer status;// 响应状态码
	private String message;// 响应消息
	private Object data;// 响应数据
	
	/**
	 * status = 200, message = "OK", data = null
	 */
	public CommonResult() {
		this.status = 200;
		this.message = "OK";
		this.data = null;
	}
	
	/**
	 * status = 200, message = "OK"
	 * @param data
	 */
	public CommonResult(Object data) {
		this.status = 200;
		this.message = "OK";
		this.data = data;
	}
	
	public CommonResult(Integer status, String message) {
		this.status = status;
		this.message = message;
		this.data = null;
	}
	
	public CommonResult(Integer status, String message, Object data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
}
