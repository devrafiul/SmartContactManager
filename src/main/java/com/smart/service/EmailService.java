package com.smart.service;

import java.security.Security;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	
	public boolean sendEmail(String subject , String message , String to ) {
		
		boolean f=false;
		//Variable for gmail
		
		String from="rafiulislam432060@gmail.com";
		
		String host="smtp.gmail.com";
		  
		//get the system properties
		
		Properties properties = System.getProperties();
		System.out.println("Properties" +properties);
		
		
		//setting important informatioin to properties object
		
		/*//host set
		 
		properties.put("mail.smtp.host",host);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.port", "465");
		//properties.put("mail.smtp.ssl.enable","true");
		properties.put("mail.smtp.auth","true");
		//properties.put("mail.smtp.ssl.protocols", "TLSv1.2"); 
		*/
		
		
		
		//host set
		
		
		properties.put("mail.smtp.host",host);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.port","465");
		properties.put("mail.smtp.ssl.enable","true");
		properties.put("mail.smtp.auth","true");
		properties.put("mail.smtp.ssl.protocols", "TLSv1.2"); 
		
		Security.setProperty("crypto.policy","unlimited");
		
		
		
		//Step 1:to get the session object..
		Session session = Session.getInstance(properties,new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication("rafiulislam432060@gmail.com","axtq kaqd rnmr nudx");
			}
			
			
			
		});
		
		session.setDebug(true);
		
		//step 2- compose the message[text,multimedia]
		
		MimeMessage m = new MimeMessage(session);
		
		
		
		try {
			
			//from email id
			
			m.setFrom( new InternetAddress(from));
			
			
			//adding recipient to message
			
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			//m.setRecipients(Message.RecipientType.TO, InternetAddress.parse("rafi432060@gmail.com"));
			 
			//adding subject to message
			m.setSubject(subject);
			
			//adding text to message
			
			//m.setText(message);
			m.setContent(message,"text/html");
			
			
			
			//send step-3
			
			Transport.send(m);
			
			System.out.println("Sent success....");
			
			
			f=true;
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return f;
		
		 
	}

	
}
