package com.myapp.root.servicebus;

import com.azure.messaging.servicebus.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class AzureHello {

    static final String connectionString = "Endpoint=sb://kensaka.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=NxERT1IBvapMNvShfXtBPaLhqlMIRZJQzfJbD/lnVag=";
    static final String queueName = "test01";
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");

    static final List<String> messages = new ArrayList<>();

    // ServiceBus Sender
    static final ServiceBusSenderClient sender = new ServiceBusClientBuilder()
            .connectionString(connectionString)
            .sender()
            .queueName(queueName)
            .buildClient();

    // ServiceBus Receiver
    static final ServiceBusProcessorClient receiverClient = new ServiceBusClientBuilder()
            .connectionString("Endpoint=sb://kensaka.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=NxERT1IBvapMNvShfXtBPaLhqlMIRZJQzfJbD/lnVag=")
            .processor()
            .queueName(queueName)
            .processMessage(AzureHello::processMessage)
            .processError(AzureHello::processError)
            .buildProcessorClient();

    public static String sendMessages() {
        var now = LocalDateTime.now();
        List<ServiceBusMessage> messages = Arrays.asList(
                new ServiceBusMessage("Hello world @" + now.format(formatter)).setMessageId("1"),
                new ServiceBusMessage("Bonjour @" + now.format(formatter)).setMessageId("2"));
        sender.sendMessages(messages);

        return messages.size() + " messages sent " + messages.stream().map(ServiceBusMessage::getBody).collect(Collectors.toList());
    }


    public static String receiveMessages() {
        receiverClient.start();
        try {
            TimeUnit.SECONDS.sleep(3); // sleep for 10 seconds to read messages.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        receiverClient.close();
        var result = "Read messages " + messages;
        messages.clear();
        return result;
    }

    private static void processMessage(ServiceBusReceivedMessageContext context) {
        ServiceBusReceivedMessage message = context.getMessage();
        messages.add("<BR>" + message.getBody());
        System.out.printf("Processing message. Session: %s, Sequence #: %s. Contents: %s%n", message.getMessageId(),
                message.getSequenceNumber(), message.getBody());
    }

    private static void processError(ServiceBusErrorContext context) {
        System.out.printf("Error when receiving messages from namespace: '%s'. Entity: '%s'%n",
                context.getFullyQualifiedNamespace(), context.getEntityPath());
    }


}
