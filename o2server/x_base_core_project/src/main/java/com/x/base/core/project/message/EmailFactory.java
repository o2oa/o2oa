package com.x.base.core.project.message;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

import com.x.base.core.project.config.Config;

public class EmailFactory {

	private EmailFactory() {
		// nothing
	}

	public static HtmlEmail htmlEmail() throws Exception {
		if (BooleanUtils.isNotTrue(Config.email().getEnable())) {
			throw new ExceptionEmailNotEnable();
		}
		HtmlEmail email = new HtmlEmail();
		init(email);
		return email;
	}

	public static ImageHtmlEmail imageHtmlEmail() throws Exception {
		if (BooleanUtils.isNotTrue(Config.email().getEnable())) {
			throw new ExceptionEmailNotEnable();
		}
		ImageHtmlEmail email = new ImageHtmlEmail();
		init(email);
		return email;
	}

	public static MultiPartEmail multiPartEmail() throws Exception {
		if (BooleanUtils.isNotTrue(Config.email().getEnable())) {
			throw new ExceptionEmailNotEnable();
		}
		MultiPartEmail email = new MultiPartEmail();
		init(email);
		return email;
	}

	public static SimpleEmail simpleEmail() throws Exception {
		if (BooleanUtils.isNotTrue(Config.email().getEnable())) {
			throw new ExceptionEmailNotEnable();
		}
		SimpleEmail email = new SimpleEmail();
		init(email);
		return email;
	}

	private static void init(Email email) throws Exception {
		email.setHostName(Config.email().getHost());
		email.setSmtpPort(Config.email().getPort());
		email.setAuthenticator(new DefaultAuthenticator(Config.email().getUser(), Config.email().getPass()));
		email.setSSLOnConnect(Config.email().getSslEnable());
		email.setFrom(Config.email().getFrom());
	}

}
