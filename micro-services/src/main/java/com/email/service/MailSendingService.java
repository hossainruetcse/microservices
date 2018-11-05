package com.email.service;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.stereotype.Service;


@Service
public class MailSendingService extends Thread {

	private Session session;
	private MimeMessage msg;
	private Properties properties;
	private final String FROM = "hossain.ruetcse@gmail.com";
	private final String FROMNAME = "micro-services";
	// private final String TO = "shakhwat.hossain.ruet@gmail.com";
	private final String SMTP_USERNAME = "shakhawat.hossain.ruet@gmail.com";
	private final String SMTP_PASSWORD = "1qazZAQ!";
	private final String HOST = "smtp.gmail.com";
	private final int PORT = 587;

	public MailSendingService() {
		properties = getEmailProperties();
		session = Session.getDefaultInstance(properties);
	}

	private Properties getEmailProperties() {
		Properties properties = System.getProperties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.port", PORT);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.auth", "true");
		return properties;
	}

	private MimeMessage getMimeMessage(String emialSubject, String toAddress, String emailBody) throws Exception {
		msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(FROM, FROMNAME));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
		msg.setSubject(emialSubject);
		msg.setContent(emailBody, "text/html");
		return msg;
	}

	public void send(String emialSubject, String toAddress, String emailBody) {
		try {
			System.out.println("Email body : "+emailBody);
			msg = getMimeMessage(emialSubject, toAddress, emailBody);
			this.start();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void send(String emialSubject, String toAddress, String emailBody, String filePath) {
		try {
			System.out.println("Email body : "+emailBody);
			msg = getMimeMessage(emialSubject, toAddress, emailBody);
			msg.setContent(getMultipart(filePath));
			this.start();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		try {
			System.out.println("Sending Email...........");
			Transport transport = session.getTransport();
			transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			System.out.println("Email has sent successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private Multipart getMultipart(String filePath) throws Exception {
		Multipart multipart = new MimeMultipart();
		BodyPart messageBodyPart = new MimeBodyPart();
        String filename = "/home/manisha/file.txt";
        DataSource source = new FileDataSource(filePath);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        multipart.addBodyPart(messageBodyPart);
        return multipart;
	}
//	public static void main(String...strings ) {
//		try {
//			new MailSendingService().send("test","hossain.ruetcse@gmail.com","this is for test perpose.");
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//		
//	}

}
