package com.GmailDao.Base;

import java.util.List;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

public class BaseMessage {

    private String fromEmailAddress = "tapasroy862@gmail.com";
    private String toEmailAddress = "chakrasayanroy@gmail.com";
    /**
     * Local instance of the {@code String} class for Message Subject.
     */
    private String messageSubject = "Test message";
    /**
     * Local instance of the {@code String} class for Message Body Content.
     */
    private String bodyText = "lorem ipsum.";

    public String getFromEmailAddress() {
        return fromEmailAddress;
    }

    public void setFromEmailAddress(String fromEmailAddress) {
        this.fromEmailAddress = fromEmailAddress;
    }

    public String getToEmailAddress() {
        return toEmailAddress;
    }

    public void setToEmailAddress(String toEmailAddress) {
        this.toEmailAddress = toEmailAddress;
    }

    public String getMessageSubject() {
        return messageSubject;
    }

    public void setMessageSubject(String messageSubject) {
        this.messageSubject = messageSubject;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String onreply(Message inboxMessage){

        String replyContent = String.format(
            "From: %s\n" +
            "To: %s\n" +
            "Subject: Re: %s\n" +
            "References: %s\n" +
            "In-Reply-To: %s\n" +
            "Content-Type: text/plain; charset=utf-8\n\n%s",
            getHeaderValue(inboxMessage, "From"),
            getHeaderValue(inboxMessage, "To"),
            getHeaderValue(inboxMessage, "Subject"),
            inboxMessage.getThreadId(),
            inboxMessage.getId(),
            getBodyText()
        );

        return replyContent;
    }

    private String getHeaderValue(Message message, String headerName) {
        List<MessagePartHeader> headers = message.getPayload().getHeaders();
        for (MessagePartHeader header : headers) {
            if (header.getName().equals(headerName)) {
                return header.getValue();
            }
        }
        return "";
    }
}
