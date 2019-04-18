package tisse.service.mail;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import tisse.dto.MailData;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

/**
 * Created by tisse on 09.07.16.
 */

@Stateless
public class MailSender {

    private static final String[] TO = {"markuszugrau@gmail.com", "o2k@mail.ru"};

    @Inject
    private Logger logger;

    @Resource(lookup = "java:/outlookMailSession")
    private Session session;

    @EJB
    private MailSettingHolder mailSettingHolder;

    @Asynchronous
    public void send(List<String> to, String subject, String body, MailData mailData) {
        if (!mailSettingHolder.getMailSetting().isEnabled()) {
            logger.warn("mail sender disabled due restore mail server {} -  {}", to, subject);
            return;
        }
        try {
            if (CollectionUtils.isNotEmpty(to)) {
                MimeMessage message = new MimeMessage(session);

                message.setFrom(new InternetAddress("BSReports@bsreports.ru"));
                for (String s : to) {
                    message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(s));
                }
                for (String s : TO) {
                    message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(s));
                }
                message.setSubject(subject);

                Multipart multipart = new MimeMultipart();
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(body, "text/html; charset=utf-8");
                multipart.addBodyPart(mimeBodyPart);

                if (null != mailData && null != mailData.getContent()) {
                    MimeBodyPart messageBodyPart = new MimeBodyPart();
                    ByteArrayDataSource source = new ByteArrayDataSource(mailData.getContent(), mailData.getType());
                    String name = mailData.getName();
                    try {
                        name = new String(mailData.getName().getBytes(Charset.defaultCharset()), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.error("", e);
                    }
                    source.setName(name);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    try {
                        String encodeText = MimeUtility.encodeText(name);
                        messageBodyPart.addHeader("Content-Type", mailData.getType() + "; name=" + encodeText);
                        messageBodyPart.addHeader("Content-Disposition", Part.ATTACHMENT + "; filename=" + encodeText);
                    } catch (UnsupportedEncodingException e) {
                        logger.error("", e);
                    }
                    multipart.addBodyPart(messageBodyPart);
                }
                message.setContent(multipart);

                Transport.send(message);

                logger.info("Mail sent.");
            } else {
                logger.warn("Mail to is empty.");
            }
        } catch (MessagingException e) {
            logger.error("", e);
        }
    }

    public boolean sendTest(List<String> to, String subject, String body) {
        try {
            if (CollectionUtils.isNotEmpty(to)) {
                MimeMessage message = new MimeMessage(session);

                message.setFrom(new InternetAddress("BSReports@alshaya.com"));
                for (String s : to) {
                    message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(s));
                }
                for (String s : TO) {
                    message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(s));
                }
                message.setSubject(subject);

                Multipart multipart = new MimeMultipart();
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(body, "text/html; charset=utf-8");
                multipart.addBodyPart(mimeBodyPart);

                message.setContent(multipart);

                Transport.send(message);

                logger.info("Mail sent.");
            } else {
                logger.warn("Mail to is empty.");
            }
            return true;
        } catch (MessagingException e) {
            logger.error("", e);
        }
        return false;
    }

    public boolean test() {
        return sendTest(Collections.singletonList(TO[0]), "test", "test");
    }

    public void logTrackInfo(String body) {
        send(Collections.singletonList(TO[0]), "log tracked", body, null);
    }

}
