package org.electricaltrainingalliance.messaging.control;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.electricaltrainingalliance.messaging.dto.EmailAmqMessageDTO;
import org.jboss.logging.Logger;

import io.quarkus.arc.log.LoggerName;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmailMessageConsumer {

    @LoggerName("org.electricaltrainingalliance.messaging.control.EmailMessageConsumer")
    Logger LOGGER;

    @Incoming("email-consumer")
    public void receiveMessage(JsonObject messagePayload) {
        LOGGER.infof("📥 EMAIL Received message: %s", messagePayload.getValue("messageId"));

        try {
            // ✅ Deserialize JSON string to DTO
            EmailAmqMessageDTO emailMessage = messagePayload.mapTo(EmailAmqMessageDTO.class);
            
            LOGGER.infof("✅ Successfully processed message [ID: %s]", emailMessage.getMessageId());
        } catch (Exception e) {
            LOGGER.error("❌ Error processing message: Invalid JSON format", e);
        }
    }
}