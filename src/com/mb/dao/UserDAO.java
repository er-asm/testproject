/**
 * @author Anand
 *
 */
package com.mb.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.mb.db.DatabaseHandler;
import com.mb.model.UserPrincipal;


public class UserDAO {
	
	private DatabaseHandler databaseHandler;
	
	public UserPrincipal getUserInfo(String email) throws SQLException, IOException {
		String sqlQuery="SELECT U.FULLANAME NAME, U.EMAIL EMAIL,U.PASSWORD PASS, IR.ROLE_NAME USER_ROLE FROM TOKO_USERS U,TOKO_ROLES IR,ROLE R WHERE R.USER_ID=U.USER_ID AND R.ROLE_ID=IR.ROLE_ID AND U.ENABLED=1 AND U.EMAIL=?";
		UserPrincipal userPrincipal=null;
		
		String uname=null, mail=null,password=null,fullname=null;
		
		try(Connection dbConnection= databaseHandler.getConnection(); PreparedStatement statement=dbConnection.prepareStatement(sqlQuery);){
			statement.setString(1, email);
			
			try(ResultSet resultSet=statement.executeQuery()) {
				ArrayList<GrantedAuthority> roleList = new ArrayList<GrantedAuthority>();
				
				while(resultSet.next()) {
					SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(
					resultSet.getString("USER_ROLE")		
					);
					roleList.add(simpleGrantedAuthority);
					fullname = resultSet.getString("NAME");
					
					mail= resultSet.getString("EMAIL");
					
					password = resultSet.getString("PASS");
					
					userPrincipal = new UserPrincipal(fullname,mail,password,roleList);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return userPrincipal;
	}

	/**
	 * @return the databaseHandler
	 */
	public DatabaseHandler getDatabaseHandler() {
		return databaseHandler;
	}

	/**
	 * @param databaseHandler the databaseHandler to set
	 */
	public void setDatabaseHandler(DatabaseHandler databaseHandler) {
		this.databaseHandler = databaseHandler;
	}

}
