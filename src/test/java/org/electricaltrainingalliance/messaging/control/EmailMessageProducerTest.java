package org.electricaltrainingalliance.messaging.control;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.electricaltrainingalliance.messaging.dto.EmailAmqMessageDTO;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(InMemoryTestResourceLifecycleManager.class)
public class EmailMessageProducerTest {

    @Inject
    @Connector("smallrye-in-memory")
    InMemoryConnector connector;

    @Inject
    EmailMessageProducer emailMessageProducer;
    

    @Test
    public void testSendMessage_Success() {

        // @SuppressWarnings("unused")
        InMemorySource<EmailAmqMessageDTO> jiraConsumer = connector.source("jira-consumer");
        InMemorySource<EmailAmqMessageDTO> smartsheetConsumer = connector.source("smartsheet-consumer");
        InMemorySource<EmailAmqMessageDTO> emailConsumer = connector.source("email-consumer");
        InMemorySink<EmailAmqMessageDTO> mirasTopic = connector.sink("miras-topic");

        EmailAmqMessageDTO testMessage = new EmailAmqMessageDTO(
                UUID.randomUUID(), "Test Subject", "Test Body",
                "sender@example.com", false,
                List.of("recipient@example.com"),
                Map.of("file1.pdf", "https://s3-bucket.com/file1.pdf"),
                java.time.Instant.now());
        // producer.send(testMessage);
        emailMessageProducer.sendMessage(testMessage).await().indefinitely();

        // ✅ Log to confirm the message is sent
        System.out.println("✅ Message sent to producer: " + testMessage);

        // THIS FAILS WITH
        // EmailMessageProducerTest.testSendMessage_Success:60 
        // ClassCast class io.smallrye.reactive.messaging.amqp.OutgoingAmqpMessage cannot 
        // be cast to class org.electricaltrainingalliance.messaging.dto.EmailAmqMessageDTO
        // EmailAmqMessageDTO mirasTopicInTheTube = mirasTopic.received().get(0).getPayload();

        // This fails because there is nothign there
        await().untilAsserted(() -> assertEquals(testMessage, mirasTopic.received().get(0).getPayload()));

        //     await().untilAsserted(() -> assertEquals(testMessage, jiraConsumer.())); //.received.get(0).getPayload()));



        //     List<EmailAmqMessageDTO> jiraReceivedMessages = jiraConsumer.received().stream()
        //             .map(m -> m.getPayload())
        //             .collect(Collectors.toList());
        //     List<EmailAmqMessageDTO> smartsheetReceivedMessages = smartsheetConsumer.received().stream()
        //             .map(m -> m.getPayload())
        //             .collect(Collectors.toList());
        //     List<EmailAmqMessageDTO> emailReceivedMessages = emailConsumer.received().stream()
        //             .map(m -> m.getPayload())
        //             .collect(Collectors.toList());

        //     assertFalse(jiraReceivedMessages.isEmpty(), "Expected a message in the sink.");
        //     assertFalse(smartsheetReceivedMessages.isEmpty(), "Expected a message in the sink.");
        //     assertFalse(emailReceivedMessages.isEmpty(), "Expected a message in the sink.");
        //     assertEquals(testMessage, jiraReceivedMessages.get(0));
        //     assertEquals(testMessage, smartsheetReceivedMessages.get(0));
        //     assertEquals(testMessage, emailReceivedMessages.get(0));
        // });

        // System.out.println("✅ Test completed successfully.");
    }
}
