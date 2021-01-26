/**
 * @author Anand
 *
 */
package com.mb.utility;

import java.io.IOException;
import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MBMailExec {
	private static final Logger logger = LoggerFactory.getLogger(MBMailExec.class);
	private PropertyLoader propertyLoader=null;

	public PropertyLoader getPropertyLoader() {
		return propertyLoader;
	}

	public void setPropertyLoader(PropertyLoader propertyLoader) {
		this.propertyLoader = propertyLoader;
	}
	
	public Boolean SendMail(String to, String content,String subject) throws IOException{
		Properties property = propertyLoader.readPropertyFile();
		String from=property.getProperty("from");
		String host=property.getProperty("host");
		String port=property.getProperty("port");
		
		Properties properties= System.getProperties();
		
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		Session session = Session.getInstance(properties,new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from,property.getProperty("password_mail"));
			}
		});
		session.setDebug(true);
		
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			message.setText(content);
			
			Transport.send(message);
		}catch(MessagingException e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

}
