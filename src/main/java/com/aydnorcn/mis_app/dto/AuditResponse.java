package com.aydnorcn.mis_app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record AuditResponse(@JsonFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime createdAt,
                            @JsonFormat(pattern = "dd.MM.yyyy HH:mm") LocalDateTime updatedAt, String createdBy,
                            String updatedBy) {
}
