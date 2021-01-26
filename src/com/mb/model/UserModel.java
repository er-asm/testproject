
/**
 * @author Anand
 *
 */
package com.mb.model;

public class UserModel {

	public UserModel() {}
	
	private int loggedInUser;
	private String lastName;
	private String firstName;
	private String selectedCountry;
	private String email;
	private int role;
	private String accessLevel;
	private String phone;
	public int getLoggedInUser() {
		return loggedInUser;
	}
	public void setLoggedInUser(int loggedInUser) {
		this.loggedInUser = loggedInUser;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getSelectedCountry() {
		return selectedCountry;
	}
	public void setSelectedCountry(String selectedCountry) {
		this.selectedCountry = selectedCountry;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public String getAccessLevel() {
		return accessLevel;
	}
	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
}
