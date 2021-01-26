/**
 * @author Anand
 *
 */
package com.mb.helper;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Properties;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mb.db.DatabaseHandler;
import com.mb.model.TokenModel;
import com.mb.model.UserModel;
import com.mb.utility.MBMailExec;
import com.mb.utility.PropertyLoader;

public class UserManagementHelper {
	private static final Logger logger = LoggerFactory.getLogger(UserManagementHelper.class);

	private DatabaseHandler databaseHandler = null;
	private PropertyLoader propertyLoader = null;
	private MBMailExec mail = null;

	/**
	 * send mail
	 */
	public Boolean sendMail(UserModel user, String subject) throws IOException, SQLException {
		Properties properties = propertyLoader.readPropertyFile();
		TokenModel tokenModel = new TokenModel();
		tokenModel.setUserId(user.getEmail());

		TokenModel genToken = createToken(tokenModel);

		String urlString = properties.getProperty("url") + genToken.getToken();
		String split[] = user.getEmail().split("\\.");

		String usermail = null;

		for (String u : split) {
			usermail = u;
			break;
		}

		usermail = usermail.substring(0, 1).toUpperCase() + usermail.substring(1);
		URL url = new URL(urlString);
		String content = "\n Hi" + usermail + ",\n\nClick link to activate " + url;

		try {
			Boolean mailSent = mail.SendMail(user.getEmail(), content, subject);
			boolean status = updateStatus(user.getEmail(), "ACTIVATION IN PROGRESS");
			logger.info("updated in db-->" + status);
			return mailSent;
		} catch (Exception e) {
			return false;
		}
	}

	public Boolean updateStatus(String onboardUserEmail, String status) throws SQLException, IOException {
		Connection conn = null;
		Statement stmt = null;
		Boolean result = false;

		try {
			conn = databaseHandler.getConnection();
			stmt = conn.createStatement();

			String sql = "UPDATE TOKO_USERS SET USER_STATUS='" + status
					+ "' WHERE (USER_STATUS='NEW' OR USER_STATUS='ACTIVATION IN PROGRESS' OR USER_STATUS='PASSWORD EXPIRED' OR USER_STATUS='INACTIVE') AND EMAIL='"
					+ onboardUserEmail;
			int row = stmt.executeUpdate(sql);
			if (row > 0) {
				result = true;
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return result;
	}

	/**
	 * Create token
	 * 
	 * @throws SQLException
	 */
	public TokenModel createToken(TokenModel tokenModel) throws SQLException {
		String token = createToken();
		tokenModel.setToken(token);
		tokenModel.setTokenStatus("ACTIVE");

		boolean result = isInsertToken(tokenModel);
		if (result) {
			tokenModel.setMessage("Token Inserted to Database");
		} else {
			tokenModel.setMessage("Token Insertion Failed");
		}
		return tokenModel;
	}

	public TokenModel verifyToken(TokenModel tokenModel) throws SQLException, IOException {
		int maxAge = getMaxAge(tokenModel);
		if (maxAge > 3) {
			tokenModel.setMessage("Token is not valid, Please create a new password reset request");
			tokenModel.setStatus("NOT VERIFIED");
			return tokenModel;
		} else {
			tokenModel.setMaxCount(maxAge + 1);
			insertTokenHitCount(tokenModel);
		}
		TokenModel result = verifyTokenFromDb(tokenModel);

		Calendar calendar = Calendar.getInstance();
		if (result.getUserId() == "0") {
			tokenModel.setMessage("Token is not valid, Please create a new password reset request");
			tokenModel.setStatus("NOT VERIFIED");
			return tokenModel;
		} else if ((result.getExpiryDate().getTime() - calendar.getTime().getTime()) > 0) {
			tokenModel.setMessage("Token is Verified");
			tokenModel.setStatus("VERIFIED");
			tokenModel.setUserId(result.getUserId());
			tokenModel.setUserName(result.getUserName());
		} else {
			tokenModel.setUserId(result.getUserId());
			tokenModel.setUserName(result.getUserName());

			String userEmail = getUserEmail(Integer.parseInt(result.getUserId()));
			updateStatus(userEmail, "INACTIVE");
		}
		return tokenModel;
	}

	public int getUserIDFromEmail(String userEmail) throws SQLException {
		String sqlQuery = null;
		ResultSet resultSet = null;
		Connection dbConnection = null;
		Statement stmt = null;
		int userid=0;
		
		try {
			dbConnection = databaseHandler.getConnection();
			stmt=dbConnection.createStatement();
			sqlQuery = "SELECT USER_ID FROM TOKO_USERS WHERE EMAIL='"+userEmail+"'";
			
			resultSet = stmt.executeQuery(sqlQuery);
			while(resultSet.next()) {
				userid = resultSet.getInt("USER_ID");
			}
		}catch(Exception e) {
			logger.error(e.getMessage());
		}finally {
			if(resultSet!=null)
				resultSet.close();
			if(stmt!=null)
				stmt.close();
			if(dbConnection!=null)
				dbConnection.close();
		}
		return userid;
	}
	/**
	 * @param parseInt
	 * @return
	 */
	private String getUserEmail(int userid) throws SQLException {
		String sqlQuery = null;
		ResultSet resultSet = null;
		Connection conn = null;
		Statement stmt = null;
		String userEmail = null;
		try {
			conn = databaseHandler.getConnection();
			stmt = conn.createStatement();
			sqlQuery = "SELECT EMAIL FROM TOKO_USERS WHERE USER_ID=" + userid;
			resultSet = stmt.executeQuery(sqlQuery);
			while (resultSet.next()) {
				userEmail = resultSet.getString("EMAIL");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (stmt != null) {
				stmt.close();
			}

			if (conn != null) {
				conn.close();
			}
			if (resultSet != null) {
				resultSet.close();
			}

		}
		return userEmail;
	}

	/**
	 * @param tokenModel
	 * @throws SQLException
	 */
	private Boolean insertTokenHitCount(TokenModel tokenModel) throws SQLException {

		Connection dbConnection = null;
		Statement stmt = null;

		try {
			dbConnection = databaseHandler.getConnection();
			stmt = dbConnection.createStatement();
			String sqlQuery = "UPDATE TOKEN_SECURITY SET MAX_COUNT='" + tokenModel.getMaxCount() + "' WHERE TOKEN='"
					+ tokenModel.getToken() + "'";
			int rowCount = stmt.executeUpdate(sqlQuery);
			if (rowCount > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (stmt != null)
				stmt.close();
			if (dbConnection != null)
				dbConnection.close();
		}
		return false;
	}

	/**
	 * @param tokenModel
	 * @return
	 * @throws SQLException
	 */
	private TokenModel verifyTokenFromDb(TokenModel tokenModel) throws SQLException {
		TokenModel result = null;
		String sqlQuery = null;
		ResultSet resultSet = null;
		String token = null;
		Date expiryDate = null;
		String email = null;
		String userid = null;
		Connection dbConnection = null;
		Statement stmt = null;

		try {
			dbConnection = databaseHandler.getConnection();
			stmt = dbConnection.createStatement();
			result = new TokenModel();
			sqlQuery = "SELECT TS.TOKEN,TS.EXPIRY_DATE,TS.USER_ID,RU.EMAIL FROM TOKEN_SECURITY TS, TOKO_USERS RU WHERE TS.TOKEN='"
					+ tokenModel.getToken() + "' AND TS.USER_ID=RU.USER_ID";
			resultSet = stmt.executeQuery(sqlQuery);
			while (resultSet.next()) {
				token = resultSet.getString("TOKEN");
				expiryDate = resultSet.getDate("EXPIRY_DATE");
				userid = resultSet.getString("USER_ID");
				email = resultSet.getString("EMAIl");

				result.setToken(token);
				result.setExpiryDate(expiryDate);
				result.setUserId(userid);
				result.setUserName(email);

			}
			if (userid == null) {
				result.setUserId("0");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			if (dbConnection != null)
				dbConnection.close();
		}

		return result;
	}

	public int getMaxAge(TokenModel tokenModel) throws SQLException {
		String sqlQUery = null;
		ResultSet resultSet = null;
		int maxAge = 0;
		Connection dbConnection = null;
		Statement stmt = null;

		try {
			dbConnection = databaseHandler.getConnection();
			stmt = dbConnection.createStatement();
			sqlQUery = "SELECT * FROM TOKEN_SECURITY WHERE TOKEN='" + tokenModel.getToken() + "'";
			resultSet = stmt.executeQuery(sqlQUery);
			while (resultSet.next()) {
				maxAge = resultSet.getInt("MAX_COUNT");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (resultSet != null)
				resultSet.close();
			if (stmt != null)
				stmt.close();
			if (dbConnection != null)
				dbConnection.close();
		}
		return maxAge;
	}

	public String createToken() {
		String token = UUID.randomUUID().toString();
		return token;
	}

	public boolean isInsertToken(TokenModel tokenModel) throws SQLException {
		Date expiryDate = calculateExpiryDate(8000000);
		tokenModel.setExpiryDate(expiryDate);
		boolean result = isInsertToken(tokenModel);
		return result;
	}

	private Date calculateExpiryDate(int expirytime) {
		return new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
	}

}
