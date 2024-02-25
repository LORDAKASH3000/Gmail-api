package com.GmailDao.MAILDao;

import java.io.IOException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.GmailDao.Base.BaseMessage;
import com.GmailDao.GmailCredentialDao.CredentialDao;
import com.GmailDao.MIMEDao.MimeDao;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

public class EmailDao {
    /**
     * Global instance of the JSON factory & network HTTP Transport.
     */
    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    /**
     * Global instance of the network HTTP Transport.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Global instance of the {@code CredentialDao} class.
     */
    private static final CredentialDao credentialDao = new CredentialDao(GmailScopes.GMAIL_MODIFY,"EmailSender_Token");
    /**
     * Global instance of the {@code com.google.api.client.auth.oauth2.Credential} class.
     */
    private static final Credential credential = credentialDao.getCredentials(HTTP_TRANSPORT);
    /**
     * Global instance of the {@code MimeDao} class.
     */
    private static final MimeDao mimeDao = new MimeDao();
    /**
     * Global instance of the {@code Gmail} class.
     */
    private final Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("Gmail samples").build();

    public void sendEmail() throws AddressException, MessagingException, IOException{
        BaseMessage msg = new BaseMessage();
        Message message = mimeDao.creatMessage(msg);

        try {
            // Create send message
            message = service.users().messages().send("me", message).execute();
            System.out.println("Message id: " + message.getId());
            System.out.println(message.toPrettyString());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            System.err.println("Error code: " + error.getCode());
            System.err.println("Error message: " + error.getMessage());
        }
    }

    public void sendReplyEmail() throws AddressException, MessagingException, IOException{
        BaseMessage msg = new BaseMessage();
        Message inboxMessage = service.users().messages().get("me", "18a3b9ca5516c88f").execute();
        Message message = new Message();

        message.setRaw(Base64.encodeBase64URLSafeString(msg.onreply(inboxMessage).getBytes()));
        try {
            // Create send message
            message = service.users().messages().send("me", message).execute();
            System.out.println("Message id: " + message.getId());
            System.out.println(message.toPrettyString());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            System.err.println("Error code: " + error.getCode());
            System.err.println("Error message: " + error.getMessage());
        }
    }

    public void getMessages() throws IOException{
        ListMessagesResponse response = service.users().messages().list("me").setMaxResults(5l).execute();
        List<Message> messages = response.getMessages();
        if (messages == null || messages.isEmpty()) {
            System.out.println("No messages found.");
        } else {
            System.out.println("Messages:");
            for (Message message : messages) {
                System.out.println(" - " + message.getId());
                System.out.println(" - " + getMessagebyID(message.getId()).getPayload().getBody().toPrettyString());
            }
        }
    }

    public Message getMessagebyID(String id) throws IOException{
        return service.users().messages().get("me",id).execute();
    }

    public static String decode(String encodedString) {
        if(encodedString == null) return null;
        byte[] decodedBytes = Base64.decodeBase64(encodedString);
        return new String(decodedBytes);
    }
}
