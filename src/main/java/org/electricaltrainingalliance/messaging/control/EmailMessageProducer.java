package org.electricaltrainingalliance.messaging.control;

import java.time.Duration;
import java.util.UUID;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.electricaltrainingalliance.messaging.dto.EmailAmqMessageDTO;
import org.jboss.logging.Logger;

import io.quarkus.arc.log.LoggerName;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.amqp.OutgoingAmqpMessage;
import io.smallrye.reactive.messaging.amqp.OutgoingAmqpMetadata;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.amqp.AmqpMessageBuilder;
import io.vertx.mutiny.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbException;

@ApplicationScoped
public class EmailMessageProducer {

    @Channel("miras-topic") // ✅ Matches application.yaml
    Emitter<OutgoingAmqpMessage<JsonObject>> emitter; // ✅ Uses `OutgoingAmqpMessage<T>` for correct metadata handling

    @Inject
    Vertx vertx; // ✅ Ensure we get a valid Vert.x Context

    @LoggerName("org.electricaltrainingalliance.messaging.control.EmailMessageProducer")
    Logger LOGGER;

    @Retry(maxRetries = 5, delay = 500, maxDuration = 10000, // ✅ Increased for better resilience
            jitter = 300, retryOn = { Exception.class }, abortOn = { IllegalArgumentException.class })
    public Uni<Void> sendMessage(EmailAmqMessageDTO emailMessage) {
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
            JsonObject jsonMessage = JsonObject.mapFrom(emailMessage);


            // ✅ Create metadata for deduplication
            OutgoingAmqpMetadata metadata = OutgoingAmqpMetadata.builder()
                    .withAddress("miras-topic")
                    .withMessageId(messageId.toString()) // ✅ Ensures deduplication
                    .build();

            // ✅ Create Vert.x AMQP Message with structured JSON body
            io.vertx.mutiny.amqp.AmqpMessage vertxMessage = AmqpMessageBuilder.create()
                    .withJsonObjectAsBody(jsonMessage) // ✅ Use structured JSON
                    .contentType("application/json") // ✅ Ensure JSON format is recognized
                    .address("miras-topic") // ✅ Explicitly set topic address
                    .build();

            // ✅ Wrap `vertxMessage` in `OutgoingAmqpMessage<T>` to correctly attach metadata
            OutgoingAmqpMessage<JsonObject> amqpMessage = new OutgoingAmqpMessage<>(vertxMessage, metadata);

            // ✅ Send Message Asynchronously
            return Uni.createFrom()
                    .completionStage(emitter.send(amqpMessage)) // ✅ Uses `OutgoingAmqpMessage<T>`, ensuring metadata is
                                                                // included
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
