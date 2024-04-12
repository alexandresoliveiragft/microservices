package dev.alexandreoliveira.microservices.accountsapi.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BaseDto {

    private UUID id;

    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    private String createdBy;

    @JsonIgnore
    private LocalDateTime updatedAt;

    @JsonIgnore
    private String updatedBy;
}
