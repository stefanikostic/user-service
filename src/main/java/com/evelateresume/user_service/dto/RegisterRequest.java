package com.evelateresume.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(@NotNull
                              @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$",
                                      message = "Username must be between 3 and 20 characters and can only contain " +
                                              "letters, numbers, and underscores.")
                              String username,
                              @NotNull
                              @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                                      message = "Password must contain at least one uppercase letter, one lowercase letter, " +
                                              "one digit, one special character, and be at least 8 characters long.")
                              String password,
                              @NotNull
                              @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$",
                                      message = "Full name can only contain alphabetic characters and spaces.")
                              String fullName,
                              @NotNull @Email String email) {
}
