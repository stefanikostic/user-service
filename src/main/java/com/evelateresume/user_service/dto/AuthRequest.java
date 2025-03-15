package com.evelateresume.user_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AuthRequest(@NotNull
                          @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$",
                                  message = "Username must be between 3 and 20 characters and can only contain " +
                                          "letters, numbers, and underscores.")
                          String username,
                          @NotNull String password) {}