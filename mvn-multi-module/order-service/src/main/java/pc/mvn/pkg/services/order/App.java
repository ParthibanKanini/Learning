package pc.mvn.pkg.services.order;

import pc.mvn.pkg.common.Money;
import pc.mvn.pkg.order.OrderCreated;

/**
 * Main entry point for the Order Service application.
 *
 * This class demonstrates how to work with Protocol Buffer (Protobuf) generated
 * classes in a Java application. Protobuf is a language-neutral,
 * platform-neutral serialization format developed by Google for structured
 * data. It's more efficient than JSON/XML in terms of both serialization speed
 * and message size.
 *
 * Key concepts demonstrated: - Builder Pattern: Protobuf uses the builder
 * pattern for object construction, allowing for optional field assignment and a
 * fluent interface. - Immutability: Once built, Protobuf messages are
 * immutable, making them thread-safe. - Schema Evolution: Changes to .proto
 * files can be made while maintaining backward compatibility.
 */
public class App {

    /**
     * Application main method that creates and displays Protobuf objects.
     *
     * This method showcases: 1. Creating a Money object (represents currency
     * amount with decimal precision) 2. Creating an OrderCreated event object
     * (represents a domain event in an event-driven architecture) 3.
     * Serializing complex nested structures using Protobuf
     *
     * @param args Command-line arguments (not used in this example)
     */
    public static void main(String[] args) {
        // Create a Money object using the builder pattern.
        // This demonstrates Protobuf's builder API which:
        // - Provides type safety at compile time
        // - Allows optional field initialization
        // - Returns a fully validated, immutable object when build() is called
        Money money = Money.newBuilder()
                .setCurrency("INR") // Currency code (ISO 4217 standard)
                .setUnits(1500) // Integer part of the amount (1500.000000000)
                .setNanos(0) // Fractional part (0 nanos = 0.000000000 units)
                .build();            // Constructs the immutable Money message

        // Create an OrderCreated event using a nested Protobuf message.
        // In event-driven architectures, events are domain occurrences published to notify
        // other services about state changes. This pattern enables loose coupling between services.
        OrderCreated evt = OrderCreated.newBuilder()
                .setOrderId("ORD-1001") // Unique identifier for the order
                .setCustomerId("CUST-7788") // Reference to the customer placing the order
                .setTotalAmount(money) // Nested Money message for the order amount
                .build();                         // Constructs the immutable OrderCreated message

        // Display the generated object in human-readable format.
        // Protobuf provides a default toString() implementation that shows all fields,
        // which is useful for debugging and logging purposes.
        System.out.println("Generated Protobuf object:");
        System.out.println(evt);
    }
}
