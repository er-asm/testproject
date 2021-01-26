package com.mb.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Anand
 *
 */
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mb.utility.PropertyLoader;

public class DatabaseHandler {

	private final static Logger logger = LoggerFactory.getLogger(DatabaseHandler.class);

	
	private DataSource dataSource;

	private PropertyLoader propertyLoader = null;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public PropertyLoader getPropertyLoader() {
		return propertyLoader;
	}

	public void setPropertyLoader(PropertyLoader propertyLoader) {
		this.propertyLoader = propertyLoader;
	}

	public Connection getConnection() throws IOException {
		Properties properties = propertyLoader.readPropertyFile();

		logger.debug("db url--->" + properties.getProperty("jdbcDriver"));

		try {
			Class.forName(properties.getProperty("jdbcDriver"));
			return DriverManager.getConnection(properties.getProperty("databaseURL"),
					properties.getProperty("db.userName"), properties.getProperty("db.password"));
		} catch (ClassNotFoundException e) {

			logger.error(e + "");
		} catch (SQLException e) {
			logger.error(e + "");
		}
		return null;
	}

}
