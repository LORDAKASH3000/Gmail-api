package com.GmailDao.GmailDraftDao;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.GmailDao.Base.BaseMessage;
import com.GmailDao.GmailCredentialDao.CredentialDao;
import com.GmailDao.MIMEDao.MimeDao;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

/**
 * @author <h4>Chakra Sayan Roy</h4>
 * 
 * The {@code DraftDao} class represents the 
 * Data Access Object for a {@code com.google.api.services.gmail.model.Draft} class.
 */
public class DraftDao {
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
    private static final CredentialDao credentialDao = new CredentialDao(GmailScopes.GMAIL_COMPOSE,"Draft_Token");
    /**
     * Global instance of the {@code com.google.api.client.auth.oauth2.Credential} class.
     */
    private static final Credential credential = credentialDao.getCredentials(HTTP_TRANSPORT);
    /**
     * Global instance of the {@code MimeDao} class.
     */
    private static final MimeDao mimeDao = new MimeDao();
    
    /**
     * Create a draft email.
     *
     * @param fromEmailAddress - Email address to appear in the from: header
     * @param toEmailAddress   - Email address of the recipient
     * @return the created {@code Draft} object, {@code null} otherwise.
     * @throws AddressException
     * @throws MessagingException - if a wrongly formatted address is encountered.
     * @throws IOException        - if service account credentials file not found.
     * @throws GeneralSecurityException
     */
    public  void createDraftMessage() throws AddressException, MessagingException, IOException{
        // Create the gmail API client
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("Gmail samples").build();
        BaseMessage msg = new BaseMessage();
        Message message = mimeDao.creatMessage(msg);
        // Create the draft message
        Draft draft = new Draft();

        draft.setMessage(message);
        try {
            draft = service.users().drafts().create("me", draft).execute();
            System.out.println("Draft id: " + draft.getId());
            System.out.println(draft.toPrettyString());
        }catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            System.err.println("Error code: " + error.getCode());
            System.err.println("Error message: " + error.getMessage());
        }
    }

    public void updateDraftMessage() throws AddressException, MessagingException, IOException{
        // Create the gmail API client
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("Gmail samples").build();
        BaseMessage msg = new BaseMessage();
        Message message = mimeDao.creatMessage(msg);

        try {
            // Fetching the draft message
            Draft draft = service.users().drafts().get("me", "r-1665462024290816064").execute();
            draft.getMessage().setRaw(message.getRaw());
            draft = service.users().drafts().update("me", "r-1665462024290816064", draft).execute();
            System.out.println("Draft id: " + draft.getId());
            System.out.println(draft.toPrettyString());

        }catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            System.err.println("Error code: " + error.getCode());
            System.err.println("Error message: " + error.getMessage());
        }
    }

    public void sendDraftMessage() throws AddressException, MessagingException, IOException{
        // Create the gmail API client
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("Gmail samples").build();
        Message message = new Message();

        try {
            // Fetching the draft message
            Draft draft = service.users().drafts().get("me", "r2256819699185855557").execute();
            message = service.users().drafts().send("me", draft).execute();
            System.out.println("Draft id: " + message.getId());
            System.out.println(message.toPrettyString());

        }catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            System.err.println("Error code: " + error.getCode());
            System.err.println("Error message: " + error.getMessage());
        }
    }
}
