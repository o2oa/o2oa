package com.x.message.assemble.communicate;

import com.google.gson.JsonElement;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.core.entity.Message;

public class EmailConsumeQueue extends AbstractQueue<Message> {

    private static Logger logger = LoggerFactory.getLogger(EmailConsumeQueue.class);

    private static final String TASK_FIRST = "first";

    protected void execute(Message message) throws Exception {
        message.setConsumed(true);
        if (Config.emailNotification().getEnable()) {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                String receiver = business.organization().person().getObject(message.getPerson()).getMail();
                JsonElement jsonElement = XGsonBuilder.instance().fromJson(message.getBody(), JsonElement.class);
                if (StringUtils.equalsIgnoreCase(message.getType(), MessageConnector.TYPE_TASK_CREATE)
                        && BooleanUtils.isFalse(XGsonBuilder.extractBoolean(jsonElement, TASK_FIRST))) {
                    logger.info("Sending notification email to {}.", receiver);
                    sendMail(receiver, message.getTitle());
                }
            }
        }
    }

    private void sendMail(String receiver, String subject) throws MessagingException, UnsupportedEncodingException, Exception {
        String server = Config.emailNotification().getServer();
        String port = Config.emailNotification().getPort();
        String user = Config.emailNotification().getUser();
        String password = Config.emailNotification().getPassword();
        String displayName = Config.emailNotification().getDisplayName();
        String subjectPrefix = Config.emailNotification().getSubjectPrefix();
        String contentHTML = Config.emailNotification().getContentHTML();

        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", server);
        props.setProperty("mail.smtp.port", port);
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.user", user);
        props.setProperty("mail.password", password);
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(user, displayName));
        message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(receiver));
        message.setSubject(subjectPrefix + subject, "UTF-8");
        message.setContent(contentHTML, "text/html; charset=UTF-8");
        Transport.send(message);
        logger.info("Notification email is sent to {}.", receiver);
    }
}
