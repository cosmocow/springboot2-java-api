package com.dfusiontech.server.services;

import com.dfusiontech.server.model.config.ApplicationProperties;
import com.dfusiontech.server.model.jpa.entity.UserPasswordResetLinks;
import com.dfusiontech.server.model.jpa.entity.Users;
import com.dfusiontech.server.rest.exception.ApplicationExceptionCodes;
import com.dfusiontech.server.rest.exception.InternalServerErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Locale;

/**
 * Email send Service
 *
 * @author   Eugene A. Kalosha <ekalosha@dfusiontech.com>
 * @version  0.1.1
 * @since    2018-12-25
 */
@Service
public class EmailService {

	@Autowired
	private ApplicationProperties applicationProperties;

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	@Qualifier("templateEngineContent")
	private TemplateEngine templateEngineContent;

	@Autowired
	@Qualifier("templateEngineFile")
	private TemplateEngine templateEngineFile;

	@Autowired
	private UserPasswordResetLinksService userPasswordResetLinksService;

	/**
	 * Send user registration email
	 *
	 * @return Answer Weights List
	 */
	public void sendUserRegistrationEmail(Users user) {

		// Create Password Reset Link with expiration in 30 days
		UserPasswordResetLinks linkDetails = userPasswordResetLinksService.create(user, 259200000l);

		String fullName = StringUtils.isEmpty(user.getFullName()) ? user.buildFullName() : user.getFullName();
		final Context ctx = new Context(Locale.ENGLISH);
		ctx.setVariable("name", fullName);
		ctx.setVariable("accountName", user.getEmail());
		ctx.setVariable("resetPasswordLink", userPasswordResetLinksService.getLinkUrl(linkDetails));
		String htmlContent = "";
		String subject = "";

		htmlContent = this.templateEngineFile.process("user-registration-email.html", ctx);
		subject = "User registration on vRisk";

		try {
			// Prepare message using a Spring helper
			final MimeMessage mimeMessage = getMimeMessage(user, subject, htmlContent);
			emailSender.send(mimeMessage);
		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new InternalServerErrorException(MessageFormat.format("Failed to send user registration email to [{0}]", user.getEmail()), ApplicationExceptionCodes.USER_REGISTRATION_EMAIL_FAILED);
		}

	}

	/**
	 * Send password reset email
	 *
	 * @return Answer Weights List
	 */
	public void sendResetPasswordEmail(Users user) {

		UserPasswordResetLinks linkDetails = userPasswordResetLinksService.create(user);

		final Context ctx = new Context(Locale.ENGLISH);
		ctx.setVariable("name", user.getFullName());
		ctx.setVariable("resetPasswordLink", userPasswordResetLinksService.getLinkUrl(linkDetails));
		final String htmlContent = this.templateEngineFile.process("forgot-password-email.html", ctx);
		final String subject = "Password Restore on vRisk";

		try {
			// Prepare message using a Spring helper
			final MimeMessage mimeMessage = getMimeMessage(user, subject, htmlContent);
			emailSender.send(mimeMessage);
		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new InternalServerErrorException(MessageFormat.format("Failed to send reset password email to [{0}]", user.getEmail()), ApplicationExceptionCodes.RESET_PASSWORD_LINK_EMAIL_FAILED);
		}

	}

	/**
	 * Build MIME Message for User and HTML content
	 *
	 * @param user
	 * @param subject
	 * @param htmlContent
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	private MimeMessage getMimeMessage(Users user, String subject, String htmlContent) throws UnsupportedEncodingException, MessagingException {
		// Prepare message using a Spring helper
		final MimeMessage mimeMessage = emailSender.createMimeMessage();

		InternetAddress fromAddress = new InternetAddress(applicationProperties.getEmailMessageFromAddress());
		InternetAddress toAddress = new InternetAddress(user.getEmail(), user.getFullName());

		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		message.setSubject(subject);
		message.setFrom(fromAddress);
		message.setReplyTo(fromAddress);
		message.setTo(toAddress);
		message.setText(htmlContent, true);
		return mimeMessage;
	}

}
