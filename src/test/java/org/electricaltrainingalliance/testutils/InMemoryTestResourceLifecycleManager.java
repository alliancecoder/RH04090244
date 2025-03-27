package org.electricaltrainingalliance.testutils;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;

public class InMemoryTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {
        Map<String, String> env = new HashMap<>();
        Map<String, String> incomingProps = InMemoryConnector.switchIncomingChannelsToInMemory("jira-consumer", "smartsheet-consumer", "email-consumer");     
        Map<String, String> outgoingProps = InMemoryConnector.switchOutgoingChannelsToInMemory("miras-topic");  
        env.putAll(incomingProps);
        env.putAll(outgoingProps);
        return env;  
    }

    @Override
    public void stop() {
        InMemoryConnector.clear();
    }
    
}
