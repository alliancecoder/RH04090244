package org.electricaltrainingalliance.messaging.boundary;

import java.time.Duration;
import java.util.UUID;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.electricaltrainingalliance.messaging.dto.MirasTopicDTO;
import org.jboss.logging.Logger;

import io.quarkus.arc.log.LoggerName;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.bind.JsonbException;

@ApplicationScoped
public class MirasTopicProducer {

    @Channel("miras-topic")
    Emitter<MirasTopicDTO> emitter;

    @LoggerName("org.electricaltrainingalliance.messaging.boundary.MirasTopicProducer")
    Logger LOGGER;

    @Retry(maxRetries = 5, delay = 500, maxDuration = 10000, // ✅ Increased for better resilience
            jitter = 300, retryOn = { Exception.class }, abortOn = { IllegalArgumentException.class })
    public Uni<Void> sendMessage(MirasTopicDTO emailMessage) {
        if (emailMessage == null) {
            LOGGER.error("Attempted to send a null email message.");
            return Uni.createFrom().failure(new IllegalArgumentException("Email message cannot be null"));
        }

        // Assign a unique message ID if not already set
        UUID messageId = emailMessage.getMessageId() != null ? emailMessage.getMessageId() : UUID.randomUUID();
        emailMessage.setMessageId(messageId);
        emailMessage.setEmailBody(emailMessage.getEmailBody() != null ? emailMessage.getEmailBody() : "");

        LOGGER.infof("📤 Sending message [ID: %s] to AMQP topic: miras-topic", messageId);

        try {
            // ✅ Send Message Asynchronously
            return Uni.createFrom()
                    .completionStage(emitter.send(emailMessage)) // ✅ Uses `POJO`
                    .ifNoItem().after(Duration.ofSeconds(5)).fail()
                    .onFailure()
                    .invoke(failure -> LOGGER.errorf("❌ Failed to send message [ID: %s]: %s", messageId,
                            failure.getMessage()))
                    .replaceWithVoid();

        } catch (JsonbException e) {
            LOGGER.error("❌ Failed to serialize EmailAmqMessageDTO using JSON-B", e);
            return Uni.createFrom().failure(e);
        }
    }
}
