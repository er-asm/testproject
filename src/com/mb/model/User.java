package com.mb.model;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Anand
 *
 */
public class User {
	public User() {
	}

	private Set<Role> roles = new HashSet<>();
	private String fullName;
	private String userName;
	private String email;
	private String password;
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	

}
