package org.electricaltrainingalliance.messaging.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import org.electricaltrainingalliance.validations.ValidEntity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.json.bind.annotation.JsonbTransient;

@RegisterForReflection
public class EmailAmqMessageDTO implements ValidEntity, Serializable {

    private static final long serialVersionUID = 42L;
    private static final Pattern URL_PATTERN = Pattern.compile("https://.+");

    private UUID messageId;
    private String subject;
    private String sender;
    private String emailBody;
    private boolean isHtml;
    private List<String> recipients;
    private Map<String, String> attachments;
    private Instant timestamp;

    public EmailAmqMessageDTO() {
        this.messageId = UUID.randomUUID();
        this.recipients = new ArrayList<>();
        this.attachments = new HashMap<>();
    }
    
    public EmailAmqMessageDTO(UUID messageId, String subject, String emailBody, String sender, boolean isHtml,
                              List<String> recipients, Map<String, String> attachments, Instant timestamp) {
        this.messageId = messageId;
        this.subject = subject;
        this.emailBody = emailBody;
        this.sender = sender;
        this.isHtml = isHtml;
        this.recipients = recipients;
        this.attachments = attachments;
        this.timestamp = timestamp;
    }


    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public boolean isHtml() {
        return isHtml;
    }

    public void setHtml(boolean isHtml) {
        this.isHtml = isHtml;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @JsonbTransient
    @Override
    public boolean isValid() {
        if (
            messageId == null
                || subject == null || subject.trim().isEmpty()
                || sender == null || sender.trim().isEmpty()
                || emailBody == null || emailBody.trim().isEmpty()
                || recipients == null || recipients.isEmpty()
                || timestamp == null) {
            return false;
        }

        if (attachments != null && !attachments.isEmpty()) {
            for (Map.Entry<String, String> entry : attachments.entrySet()) {
                String fileName = entry.getKey();
                String url = entry.getValue();
                if (fileName == null || fileName.trim().isEmpty() || url == null || !URL_PATTERN.matcher(url).matches()) {
                    return false;
                }
            }
        }

        return true;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EmailAmqMessageDTO other = (EmailAmqMessageDTO) obj;
        return isHtml == other.isHtml &&
                Objects.equals(messageId, other.messageId) &&
                Objects.equals(subject, other.subject) &&
                Objects.equals(sender, other.sender) &&
                Objects.equals(emailBody, other.emailBody) &&
                Objects.equals(recipients, other.recipients) &&
                Objects.equals(timestamp, other.timestamp);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(messageId, subject, sender, emailBody, isHtml, recipients, timestamp);
    }
    
}
