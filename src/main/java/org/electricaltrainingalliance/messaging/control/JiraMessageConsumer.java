package org.electricaltrainingalliance.messaging.control;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.electricaltrainingalliance.messaging.dto.EmailAmqMessageDTO;
import org.jboss.logging.Logger;

import io.quarkus.arc.log.LoggerName;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JiraMessageConsumer {

    @LoggerName("org.electricaltrainingalliance.messaging.control.JiraMessageConsumer")
    Logger LOGGER;

    @Incoming("jira-consumer")
    public void receiveMessage(JsonObject messagePayload) {
        LOGGER.infof("📥 JIRA Received message: %s", messagePayload.getValue("messageId"));
        
        try {
            // ✅ Deserialize JSON string to DTO
            EmailAmqMessageDTO emailMessage = messagePayload.mapTo(EmailAmqMessageDTO.class);
            
            LOGGER.infof("✅ Successfully processed message [ID: %s]", emailMessage.getMessageId());
        } catch (Exception e) {
            LOGGER.error("❌ Error processing message: Invalid JSON format", e);
        }
    }
}
