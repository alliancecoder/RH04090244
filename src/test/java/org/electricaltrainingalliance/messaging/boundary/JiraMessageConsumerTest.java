package org.electricaltrainingalliance.messaging.boundary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.electricaltrainingalliance.messaging.dto.MirasTopicDTO;
import org.electricaltrainingalliance.testutils.InMemoryTestResourceLifecycleManager;
import org.electricaltrainingalliance.testutils.TestLogHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(InMemoryTestResourceLifecycleManager.class)
public class JiraMessageConsumerTest {

    @Inject
    @Connector("smallrye-in-memory")
    InMemoryConnector connector;

    @Inject
    MirasTopicProducer mirasTopicProducer;

    private TestLogHandler logHandler;
    private java.util.logging.Logger julLogger = java.util.logging.Logger.getLogger("org.electricaltrainingalliance.messaging.boundary.JiraMessageConsumer");

    @BeforeEach
    void setup() {
        // Access the JUL logger that underpins the JBoss logger
        julLogger = java.util.logging.Logger.getLogger("org.electricaltrainingalliance.messaging.boundary");
        logHandler = new TestLogHandler();
        julLogger.addHandler(logHandler);
    }

    @AfterEach
    void teardown() {
        julLogger.removeHandler(logHandler);
    }

    @Test
    public void testJiraConsumer_success() {

        InMemorySource<MirasTopicDTO> jiraConsumer = connector.source("jira-consumer");
        InMemorySink<MirasTopicDTO> mirasTopic = connector.sink("miras-topic");

        UUID messageId = UUID.randomUUID();
        MirasTopicDTO testMessage = new MirasTopicDTO(
                messageId, "Test Subject", "Test Body",
                "sender@example.com", false,
                List.of("recipient@example.com"),
                Map.of("file1.pdf", "https://s3-bucket.com/file1.pdf"),
                java.time.Instant.now());

        System.out.println("✅ Message sent to consumer: " + testMessage);
        mirasTopicProducer.sendMessage(testMessage).await().indefinitely();
        
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {

            assertFalse(mirasTopic.received().isEmpty());
            
            MirasTopicDTO message = mirasTopic.received().get(0).getPayload();
            jiraConsumer.send(message);

            await().untilAsserted(() -> {
                assertThat(logHandler.getMessages())
                    .anyMatch(msg -> msg.contains("JIRA Received message") && msg.contains(messageId.toString()));
                assertThat(logHandler.getMessages())
                    .anyMatch(msg -> msg.contains("Successfully processed message") && msg.contains(messageId.toString()));
            });
        });
    }    
}
