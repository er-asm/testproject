package com.mb.model;

/**
 * @author Anand
 *
 */

public class JwtAuthenticationResponse {
	public JwtAuthenticationResponse() {
	}
	private String accessToken;
	private String tokenType="Bearer";
	private String status;
	private String message;
	
	
	/**
	 * @param accessToken
	 * @param tokenType
	 * @param status
	 * @param message
	 */
	public JwtAuthenticationResponse( String tokenType, String status, String message) {
		super();
		
		this.tokenType = tokenType;
		this.status = status;
		this.message = message;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getTokenType() {
		return tokenType;
	}
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
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
	
	
}
