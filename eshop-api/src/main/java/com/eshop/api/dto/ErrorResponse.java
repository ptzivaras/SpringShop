package com.eshop.api.dto;

import lombok.*;
import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private Instant timestamp;
    private int status;              // HTTP status code (e.g., 400, 404)
    private String error;            // Reason phrase (e.g., "Bad Request")
    private String message;          // Human-friendly message
    private String path;             // Request path
    private Map<String, String> errors; // Optional: field validation errors
}
