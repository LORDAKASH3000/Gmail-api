package com.GmailDao.MIMEDao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.binary.Base64;

import com.GmailDao.Base.BaseMessage;
import com.google.api.services.gmail.model.Message;

public class MimeDao {
    private static final Properties props = new Properties();

    public Message creatMessage(BaseMessage message) throws AddressException, MessagingException, IOException {
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(message.getFromEmailAddress()));
        email.addRecipient(javax.mail.Message.RecipientType.TO,new InternetAddress(message.getToEmailAddress()));
        email.setSubject(message.getMessageSubject());
        email.setText(message.getBodyText());
        return getEncodedMessage(email);
    }

    public Message getEncodedMessage(MimeMessage email) throws IOException, MessagingException{
        Message message = new Message();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        message.setRaw(encodedEmail);
        return message;
    }
}
