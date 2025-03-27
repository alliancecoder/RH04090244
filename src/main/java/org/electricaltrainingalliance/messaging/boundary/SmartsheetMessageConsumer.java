package org.electricaltrainingalliance.messaging.boundary;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.electricaltrainingalliance.messaging.dto.MirasTopicDTO;
import org.jboss.logging.Logger;

import io.quarkus.arc.log.LoggerName;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SmartsheetMessageConsumer {

    @LoggerName("org.electricaltrainingalliance.messaging.boundary.SmartsheetMessageConsumer")
    Logger LOGGER;

    @Incoming("smartsheet-consumer")
    public void receiveMessage(MirasTopicDTO message) {
        LOGGER.infof("📥 SMARTSHEET Received message: %s", message.getMessageId());

        try {
            LOGGER.infof("✅ Successfully processed message [ID: %s]", message.getMessageId());
        } catch (Exception e) {
            LOGGER.error("❌ Error processing message: Invalid JSON format", e);
        }
    }
}
