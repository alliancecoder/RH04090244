package org.electricaltrainingalliance.messaging.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MirasTopicDTOTest {

    @Test
    void testValidEmailMessage() {
        MirasTopicDTO message = new MirasTopicDTO(
                UUID.randomUUID(),
                "Test Subject",
                "Test Body",
                "sender@example.com",
                false,
                List.of("recipient@example.com"),
                Map.of("file1.pdf", "https://s3-bucket.com/file1.pdf"),
                Instant.now());

        assertTrue(message.isValid(), "Valid EmailAmqMessageDTO should return true.");
    }

    @Test
    void testMissingMessageId() {
        MirasTopicDTO message = new MirasTopicDTO(
                null,
                "Test Subject",
                "Test Body",
                "sender@example.com",
                false,
                List.of("recipient@example.com"),
                Map.of("file1.pdf", "https://s3-bucket.com/file1.pdf"),
                Instant.now());

        assertFalse(message.isValid(), "Missing messageId should return false.");
    }

    @Test
    void testMissingSubject() {
        MirasTopicDTO message = new MirasTopicDTO(
                UUID.randomUUID(),
                null,
                "Test Body",
                "sender@example.com",
                false,
                List.of("recipient@example.com"),
                Map.of("file1.pdf", "https://s3-bucket.com/file1.pdf"),
                Instant.now());

        assertFalse(message.isValid(), "Missing subject should return false.");
    }

    @Test
    void testEmptyBody() {
        MirasTopicDTO message = new MirasTopicDTO(
                UUID.randomUUID(),
                "Test Subject",
                "",
                "sender@example.com",
                false,
                List.of("recipient@example.com"),
                Map.of("file1.pdf", "https://s3-bucket.com/file1.pdf"),
                Instant.now());

        assertFalse(message.isValid(), "Empty body should return false.");
    }

    @Test
    void testNoRecipients() {
        MirasTopicDTO message = new MirasTopicDTO(
                UUID.randomUUID(),
                "Test Subject",
                "Test Body",
                "sender@example.com",
                false,
                List.of(),
                Map.of("file1.pdf", "https://s3-bucket.com/file1.pdf"),
                Instant.now());

        assertFalse(message.isValid(), "No recipients should return false.");
    }

    @Test
    void testInvalidAttachmentUrls() {
        MirasTopicDTO message = new MirasTopicDTO(
                UUID.randomUUID(),
                "Test Subject",
                "Test Body",
                "sender@example.com",
                false,
                List.of("recipient@example.com"),
                Map.of("file1.pdf", "invalid-url"),
                Instant.now());

        assertFalse(message.isValid(), "Invalid attachment URL should return false.");
    }

    @Test
    void testMissingTimestamp() {
        MirasTopicDTO message = new MirasTopicDTO(
                UUID.randomUUID(),
                "Test Subject",
                "Test Body",
                "sender@example.com",
                false,
                List.of("recipient@example.com"),
                Map.of("file1.pdf", "https://s3-bucket.com/file1.pdf"),
                null);

        assertFalse(message.isValid(), "Missing timestamp should return false.");
    }

    @Test
    void testEmptyFilenameInAttachments() {
        MirasTopicDTO message = new MirasTopicDTO(
                UUID.randomUUID(),
                "Test Subject",
                "Test Body",
                "sender@example.com",
                false,
                List.of("recipient@example.com"),
                Map.of("", "https://s3-bucket.com/file1.pdf"),
                Instant.now());

        assertFalse(message.isValid(), "Empty filename in attachments should return false.");
    }

    @Test
    void testNullAttachmentMap() {
        MirasTopicDTO message = new MirasTopicDTO(
                UUID.randomUUID(),
                "Test Subject",
                "Test Body",
                "sender@example.com",
                false,
                List.of("recipient@example.com"),
                null,
                Instant.now());

        assertTrue(message.isValid(), "Null attachments should still return true.");
    }
}
