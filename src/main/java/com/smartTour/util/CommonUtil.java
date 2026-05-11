package com.smartTour.util;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.smartTour.model.UserDtls;
import com.smartTour.service.UserService;
//
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

	@Autowired
	private  JavaMailSender mailSender;

	@Autowired
	private UserService userService;


	public Boolean sendMail(String url, String reciepentEmail) throws UnsupportedEncodingException, MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("vicky03aman@gmail.com", "Smart guide");
		helper.setTo(reciepentEmail);

		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
				+ "\">Change my password</a></p>";
		helper.setSubject("Password Reset");
		helper.setText(content, true);
		mailSender.send(message);
		return true;
	}
	
	public Boolean sendContactMail(String name, String email, String subject, String messageText)
	        throws UnsupportedEncodingException, MessagingException {

	    MimeMessage message = mailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message, true);

	    // 🔹 Sender (your system email)
	    helper.setFrom("vicky03aman@gmail.com", "Smart Tour Support");

	    // 🔹 Receiver (your admin email OR same email)
	    helper.setTo("vicky03boss@gmail.com");
	    
	    helper.setReplyTo(email);

	    // 🔹 Subject
	    helper.setSubject("New Contact Form Submission - " + subject);

	    // 🔹 Email Content (HTML)
	    String content =
	            "<div style='font-family: Arial, sans-serif; padding: 10px;'>"
	            + "<h2 style='color:#0d6efd;'>New Contact Message</h2>"
	            + "<hr>"
	            + "<p><b>Name:</b> " + name + "</p>"
	            + "<p><b>Email:</b> " + email + "</p>"
	            + "<p><b>Subject:</b> " + subject + "</p>"
	            + "<p><b>Message:</b></p>"
	            + "<p style='background:#f8f9fa; padding:10px; border-radius:5px;'>"
	            + messageText + "</p>"
	            + "<br>"
	            + "<p style='font-size:12px;color:gray;'>This message was sent from Smart Tour Contact Form</p>"
	            + "</div>";

	    helper.setText(content, true);

	    mailSender.send(message);

	    return true;
	}

	public static String generateUrl(HttpServletRequest request) {

		// http://localhost:8080/forgot-password
		String siteUrl = request.getRequestURL().toString();

		return siteUrl.replace(request.getServletPath(), "");

	}

	
//	String msg=null;;
//	
//	public Boolean sendMailForProductOrder(ProductOrder order,String status) throws Exception
//	{
//		
//		msg="<p>Hello [[name]],</p>"
//				+ "<p>Thank you order <b>[[orderStatus]]</b>.</p>"
//				+ "<p><b>Product Details:</b></p>"
//				+ "<p>Name : [[productName]]</p>"
//				+ "<p>Category : [[category]]</p>"
//				+ "<p>Quantity : [[quantity]]</p>"
//				+ "<p>Price : [[price]]</p>"
//				+ "<p>Payment Type : [[paymentType]]</p>";
//		
//		MimeMessage message = mailSender.createMimeMessage();
//		MimeMessageHelper helper = new MimeMessageHelper(message);
//
//		helper.setFrom("vicky03arya@gmail.com", "Shooping Cart");
//		helper.setTo(order.getOrderAddress().getEmail());
//
//		msg=msg.replace("[[name]]",order.getOrderAddress().getFirstName());
//		msg=msg.replace("[[orderStatus]]",status);
//		msg=msg.replace("[[productName]]", order.getProduct().getTitle());
//		msg=msg.replace("[[category]]", order.getProduct().getCategory());
//		msg=msg.replace("[[quantity]]", order.getQuantity().toString());
//		msg=msg.replace("[[price]]", order.getPrice().toString());
//		msg=msg.replace("[[paymentType]]", order.getPaymentType());
//		
//		helper.setSubject("Product Order Status");
//		helper.setText(msg, true);
//		mailSender.send(message);
//		return true;
//	}
	
	public UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserByEmail(email);
		return userDtls;
	}
	

}
