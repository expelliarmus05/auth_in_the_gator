package com.example.authInTheGator.entity.data;

import com.example.authInTheGator.entity.enums.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
    private String email;
    private String password;
    private String name;
    private VerificationType verificationType = VerificationType.LINK; // Default to link
}
