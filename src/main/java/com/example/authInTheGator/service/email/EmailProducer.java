package com.example.authInTheGator.service.email;

import com.example.authInTheGator.entity.data.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailProducer {
    private JmsTemplate jmsTemplate;
    public EmailProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
    @Value("activemq.queue.name")
    private String queueName;

    public void sendEmailMessage(EmailMessage emailMessage) {
        try {
            jmsTemplate.convertAndSend(queueName, emailMessage);
            log.info("Email message sent to queue for: {}", emailMessage.getTo());
        } catch (Exception e) {
            log.error("Failed to send message to queue: {}", e.getMessage());
            throw new RuntimeException("Failed to send message to queue", e);
        }
    }
}
