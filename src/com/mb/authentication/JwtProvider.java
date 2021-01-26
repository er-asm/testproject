package com.mb.authentication;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import javax.xml.bind.DatatypeConverter;

/**
 * @author Anand
 *
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mb.helper.UserManagementHelper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;


public class JwtProvider {
	private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);
	private String jwtPrivateKey="MIIBOgIBAAJBAJbnaGfzVTdLpY9mZMSThrHGWgb3EceSQDlPe/CsuMx+0sgsisw2\r\n"
			+ "Qm9r8XujtdKqhGjgHYZe+hIeyluSNXoP3BMCAwEAAQJAIGikgKa+53hEn06TV7CD\r\n"
			+ "XgzGavaHCAB98JEEgkTmD2zbgvIJI/3ynLeqHpNpBxfbdefrgD7n9PM08Q8sSQ7t\r\n"
			+ "8QIhAOx2LMd/RZSp+Ed4xhJWTx/Oyacq6K6QZoKjEeN0RdK7AiEAo19yzVuwcet1\r\n"
			+ "1NvAWDIBYyaxw77RNubqm+12o1Hr4okCIDgUguRBjAgJE6gnvZolvBhIG804wPx9\r\n"
			+ "pMJA2IJAVjr3AiBEf4KM48KpIQY4hyKwV9cJEXI7Fkh18n+vfPLfKzvxsQIhAJkD\r\n"
			+ "sAq8NoIXTb6iIqUnpR6/f48+ApuT1mTHOtwuwXeU";
	
	private String jwtPublicKey="MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJbnaGfzVTdLpY9mZMSThrHGWgb3EceS\r\n"
			+ "QDlPe/CsuMx+0sgsisw2Qm9r8XujtdKqhGjgHYZe+hIeyluSNXoP3BMCAwEAAQ==";
	
	private UserManagementHelper userManagementHelper;
	
	
	public UserManagementHelper getUserManagementHelper() {
		return userManagementHelper;
	}
	public void setUserManagementHelper(UserManagementHelper userManagementHelper) {
		this.userManagementHelper = userManagementHelper;
	}
	@Value("$app.jwtExpirationInMS")
	private int jwtExpirationInMS;
	
	//retrieve username from token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}
	public Date getexpirationDateFromToken(String token) {
		return getClaimFromToken(token,Claims::getExpiration);
	}
	public <T> T getClaimFromToken(String token,Function<Claims,T> claimsResolver) {
		final Claims claims= getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}
	public Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(jwtPublicKey)).parseClaimsJws(token).getBody();
	}
	private Boolean isTokenExpired(String token) {
		final Date expiration = getexpirationDateFromToken(token);
		return expiration.before(new Date());
	}
	public String generateToken(UserDetails userDetails) {
		Map<String,Object> claims=new HashMap<>();
		Set<String> Userroles = new HashSet<>();
		for(GrantedAuthority role:userDetails.getAuthorities()) {
			Userroles.add(role.getAuthority());
		}
		claims.put("Roles", Userroles.toArray());
		try {
			claims.put("userId",userManagementHelper.getUserIDFromEmail(userDetails.getUsername()) );
		}catch(SQLException e) {
			logger.error(e.getMessage());
		}
		return doGenerateToken(claims,userDetails.getUsername());
	}
	/**
	 * @param claims
	 * @param username
	 * @return
	 */
	private String doGenerateToken(Map<String, Object> claims, String subject) {
		
		return Jwts.builder().setId(UUID.randomUUID().toString())
				.setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis() + jwtExpirationInMS*1000))
				.setExpiration(new Date(System.currentTimeMillis()+ jwtExpirationInMS*1000))
				.signWith(SignatureAlgorithm.HS512, jwtPublicKey).compact();
	}
	
	public Boolean validateToken(String token,UserDetails userDetails) {
		boolean isValid = false;
		try {
			final String userName = getUsernameFromToken(token);
			isValid = (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
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
		return isValid;
	}
}
