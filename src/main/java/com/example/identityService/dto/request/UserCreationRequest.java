package com.example.identityService.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3, message = "UserName_Invalid")
    String userName;

    @Size(min = 8, message = "Password_Invalid")
    String password;

    String email;
    LocalDate dob;


}
