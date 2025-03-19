# Red Hat Case Reproducer for RH-04090244

This project uses Quarkus and amqp and is attempting to implement
the in-memory-amqp lifecycle test.

## REPRODUCE THE FAILURE

`mvn clean test -Dtest=EmailMessageProducerTest`