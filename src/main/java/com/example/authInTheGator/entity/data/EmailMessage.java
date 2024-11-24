package com.example.authInTheGator.entity.data;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailMessage implements Serializable {
    private String to;
    private String from;
    private String cc;
    private String subject;

    public EmailMessage(String from, String subject, String body) {
        this.from = from;
        this.subject = subject;
        this.body = body;
    }

    private String body;
}
