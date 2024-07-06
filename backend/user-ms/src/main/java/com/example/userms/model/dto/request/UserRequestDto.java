package com.example.userms.model.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequestDto {
    @NotBlank(message = "Username cannot be blank")
    String username;


    @Email(message = "Email is not in a correct format")
    String email;

    @Size(min = 6, message = "Password should be at least 6 characters long")
    @Pattern(regexp = ".*\\d.*", message = "Password should contain at least 1 digit")
    String password;

}
