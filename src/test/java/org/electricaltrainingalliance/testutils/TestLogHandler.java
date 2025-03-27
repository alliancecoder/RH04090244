package org.electricaltrainingalliance.testutils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class TestLogHandler extends Handler {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void publish(LogRecord record) {
        messages.add(record.getMessage());
    }

    @Override public void flush() {}
    @Override public void close() throws SecurityException {}

    public List<String> getMessages() {
        return messages;
    }
}
