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
public class EmailAmqMessageDTOTest {

    @Test
    void testValidEmailMessage() {
        EmailAmqMessageDTO message = new EmailAmqMessageDTO(
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
        EmailAmqMessageDTO message = new EmailAmqMessageDTO(
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
        EmailAmqMessageDTO message = new EmailAmqMessageDTO(
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
        EmailAmqMessageDTO message = new EmailAmqMessageDTO(
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
        EmailAmqMessageDTO message = new EmailAmqMessageDTO(
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
        EmailAmqMessageDTO message = new EmailAmqMessageDTO(
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
        EmailAmqMessageDTO message = new EmailAmqMessageDTO(
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
        EmailAmqMessageDTO message = new EmailAmqMessageDTO(
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
        EmailAmqMessageDTO message = new EmailAmqMessageDTO(
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
