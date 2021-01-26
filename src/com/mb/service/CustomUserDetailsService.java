
/**
 * @author Anand
 *
 */
package com.mb.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.mb.dao.UserDAO;
import com.mb.model.UserPrincipal;

public class CustomUserDetailsService implements UserDetailsService {

	private UserDAO userDAO;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserDetails userDetails = null;
		try {
			UserPrincipal userPrincipal = userDAO.getUserInfo(email);

			if (userPrincipal == null) {
				throw new UsernameNotFoundException(String.format("The user %s doesn't exist", email));
			} else {
				List<GrantedAuthority> authorities = new ArrayList<>();
				userPrincipal.getAuthorities()
						.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getAuthority())));
				userDetails = new User(userPrincipal.getEmail(), userPrincipal.getPassword(), authorities);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userDetails;
	}

	/**
	 * @return the userDAO
	 */
	public UserDAO getUserDAO() {
		return userDAO;
	}

	/**
	 * @param userDAO the userDAO to set
	 */
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

}
