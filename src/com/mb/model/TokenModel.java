/**
 * @author Anand
 *
 */
package com.mb.model;

import java.sql.Date;

public class TokenModel {

	public TokenModel() {}
	
	private String userId;
	private String userName;
	private String token;
	private String status;
	private String message;
	private String tokenStatus;
	private Date expiryDate;
	private int maxCount;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTokenStatus() {
		return tokenStatus;
	}
	public void setTokenStatus(String tokenStatus) {
		this.tokenStatus = tokenStatus;
	}
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	public int getMaxCount() {
		return maxCount;
	}
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
	
	
}
