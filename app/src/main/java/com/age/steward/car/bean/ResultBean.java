package com.age.steward.car.bean;

/**
 * 
 * http请求返回结果bean
 */

public class ResultBean {
	private int result;  //结果类型： -1 未登录，0 执行出错， 1 执行成功
	private String errmsg; //执行结果不为1时的错误信息
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	
}
