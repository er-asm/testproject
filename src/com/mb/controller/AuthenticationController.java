/**
 * @author Anand
 *
 */
package com.mb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mb.authentication.JwtProvider;
import com.mb.decryption.core.DecryptionCore;
import com.mb.model.JwtAuthenticationResponse;
import com.mb.model.LoginRequest;
import com.mb.service.CustomUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@CrossOrigin
@RestController
public class AuthenticationController {
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtProvider tokenProvider;
	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request,
			HttpServletResponse response) {

		String token = null;

		try {
			DecryptionCore core = new DecryptionCore();
			UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getUsername());

			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
			logger.info("Decrypted password---->"+ core.decrypt(userDetails.getPassword(), "TOKO"));
			token = tokenProvider.generateToken(userDetails);
		} catch (DisabledException e) {
			return ResponseEntity.ok(new JwtAuthenticationResponse("", "FAILURE", "USER DISABLED"));
		} catch (BadCredentialsException e) {
			return ResponseEntity.ok(new JwtAuthenticationResponse("", "FAILURE", "INVALID CREDENTIALS"));
		} catch (SignatureException e) {
			return ResponseEntity.ok(new JwtAuthenticationResponse("", "FAILURE", "INVALID JWT SIGNATURE"));
		} catch (ExpiredJwtException e) {
			return ResponseEntity.ok(new JwtAuthenticationResponse("", "FAILURE", "JWT TOKEN EXPIRED"));
		} catch (UnsupportedJwtException e) {
			return ResponseEntity.ok(new JwtAuthenticationResponse("", "FAILURE", "UNSUPPORTED JWT TOKEN"));
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.ok(new JwtAuthenticationResponse("", "FAILURE", "USER NOT FOUND"));
		}
		return ResponseEntity.ok(new JwtAuthenticationResponse(token, "SUCCESS", "TOKEN GENERATED SUCCESSFULLY"));
	}

	/**
	 * @return the encoder
	 */
	public PasswordEncoder getEncoder() {
		return encoder;
	}

	/**
	 * @param encoder the encoder to set
	 */
	public void setEncoder(PasswordEncoder encoder) {
		this.encoder = encoder;
	}

	/**
	 * @return the authenticationManager
	 */
	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	/**
	 * @param authenticationManager the authenticationManager to set
	 */
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	/**
	 * @return the tokenProvider
	 */
	public JwtProvider getTokenProvider() {
		return tokenProvider;
	}

	/**
	 * @param tokenProvider the tokenProvider to set
	 */
	public void setTokenProvider(JwtProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	/**
	 * @return the customUserDetailsService
	 */
	public CustomUserDetailsService getCustomUserDetailsService() {
		return customUserDetailsService;
	}

	/**
	 * @param customUserDetailsService the customUserDetailsService to set
	 */
	public void setCustomUserDetailsService(CustomUserDetailsService customUserDetailsService) {
		this.customUserDetailsService = customUserDetailsService;
	}

}
