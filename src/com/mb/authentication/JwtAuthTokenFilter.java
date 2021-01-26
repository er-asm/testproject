package com.mb.authentication;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
/**
 * @author Anand
 *
 */
import org.springframework.web.filter.OncePerRequestFilter;

import com.mb.service.CustomUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;


public class JwtAuthTokenFilter extends OncePerRequestFilter {
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthTokenFilter.class);
	@Autowired
	private JwtProvider tokenProvider;
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String requestTokenHeader =((HttpServletRequest) request).getHeader(org.springframework.http.HttpHeaders.AUTHORIZATION);
		
		logger.info("request token header--"+requestTokenHeader);
		
		String username=null;
		String jwtToken=null;
		
		if(requestTokenHeader!=null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			
			try {
				logger.info("bearer is present ---> proceeding....");
				username=tokenProvider.getUsernameFromToken(jwtToken);
				
			}catch(SignatureException e) {
				logger.error("INVALID JWT SIGNATURE");
			}catch(MalformedJwtException e) {
				logger.error("INVALID JWT TOKEN");
			}catch(ExpiredJwtException e) {
				logger.error("EXPIRED JWT TOKEN");
			}catch(UnsupportedJwtException e) {
				logger.error("UNSUPPORTED JWT TOKEN");
			}catch(IllegalArgumentException e) {
				logger.error("JWT CLAIMS STRING IS EMPTY");
			}
		}else {
			throw new JwtException("JWT Token Doesn't Begin With Bearer String");
		}
		
		if(username!=null & SecurityContextHolder.getContext().getAuthentication()==null) {
			jwtToken = requestTokenHeader.substring(7);
			
			UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);
			
			if(tokenProvider.validateToken(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
				
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		filterChain.doFilter(request, response);
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
