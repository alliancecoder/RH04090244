package org.electricaltrainingalliance.messaging.boundary;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.electricaltrainingalliance.messaging.dto.MirasTopicDTO;
import org.electricaltrainingalliance.testutils.InMemoryTestResourceLifecycleManager;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(InMemoryTestResourceLifecycleManager.class)
public class MirasTopicProducerTest {

    @Inject
    @Connector("smallrye-in-memory")
    InMemoryConnector connector;

    @Inject
    MirasTopicProducer mirasTopicProducer;

    @Test
    public void testSendMessage_Success() {

        InMemorySink<MirasTopicDTO> mirasTopic = connector.sink("miras-topic");

        MirasTopicDTO testMessage = new MirasTopicDTO(
                UUID.randomUUID(), "Test Subject", "Test Body",
                "sender@example.com", false,
                List.of("recipient@example.com"),
                Map.of("file1.pdf", "https://s3-bucket.com/file1.pdf"),
                java.time.Instant.now());

        System.out.println("✅ Message sent to producer: " + testMessage);
        mirasTopicProducer.sendMessage(testMessage).await().indefinitely();

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {

            assertFalse(mirasTopic.received().isEmpty());
            
            MirasTopicDTO message = mirasTopic.received().get(0).getPayload();
            assertEquals(testMessage, message);
        });

        System.out.println("✅ Test completed successfully.");
    }

    @Test
    public void testSendMessage_FailureOnNullPayload() {
        assertThrows(IllegalArgumentException.class, () -> {
            mirasTopicProducer.sendMessage(null).await().indefinitely();
        });
    }
}