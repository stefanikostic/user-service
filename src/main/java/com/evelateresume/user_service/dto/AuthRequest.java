package com.evelateresume.user_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AuthRequest(@NotNull
                          @Pattern(regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$", message = "Full name can only contain alphabetic characters and spaces.")
                          String username,
                          @NotNull String password) {}