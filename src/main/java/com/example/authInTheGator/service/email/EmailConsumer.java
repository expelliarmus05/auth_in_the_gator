package com.example.authInTheGator.service.email;

import com.example.authInTheGator.entity.data.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailConsumer {
    private EmailService emailService;

    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }
    @JmsListener(destination = "${activemq.queue.name}")
    public void receiveEmailMessage(EmailMessage emailMessage) {
        log.info("Received email message from queue for: {}", emailMessage.getTo());
        emailService.sendEmail(emailMessage);
    }

}
