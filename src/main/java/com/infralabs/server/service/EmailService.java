package com.infralabs.server.service;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.infralabs.server.domain.Alert;

@Service
public class EmailService {

	@Value("${admin.user.email}")
	public String emailId;
	@Value("${admin.user.password}")
	public String password;

	public String sendAlert(Alert alert) {
		List<String> allMails = alert.getSendTo();// change accordingly
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		for (String to : allMails) {

			Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(emailId, password);// change
																			// accordingly
				}
			});

			try {
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(emailId));// change
																// accordingly
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				message.setSubject(alert.getSubject());
				message.setText(alert.getBody());

				// send message
				Transport.send(message);
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
		}
		return "success";
	}

}
