package com.mb.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Anand
 *
 */
public class PropertyLoader {
	private final static Logger logger= LoggerFactory.getLogger(PropertyLoader.class);
	private Properties props=null;
	public Properties getProps() {
		return props;
	}
	public void setProps(Properties props) {
		this.props = props;
	}
	public Properties readPropertyFile() throws IOException{
		try {
			InputStream inputStream = PropertyLoader.class.getResourceAsStream("/config.properties");
			if(inputStream != null) {
				props.load(inputStream);
			}
		}catch(Exception e) {
			logger.error("No properties Found "+e);
		}
		return props;
	}
}
