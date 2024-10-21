package com.crm.smsmanagementservice.provider;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-07, Sunday
 */
@Builder
public record ProviderMessagingDTO(
        @NotNull(message = "messageItems cannot be null")
        @NotEmpty(message = "messageItems cannot be empty")
        Map<@NonNull String, @Valid MessageItemDTO> messageItems,
        @Nullable
        ZonedDateTime scheduledDate,
        @Nullable
        List<String> media
) {
        @Builder
        public record MessageItemDTO(
                @NotNull(message = "Message content cannot be null")
                @NotBlank(message = "Message content cannot be blank")
                String content,
                @NotNull(message = "Recipient cannot be null")
                @NotBlank(message = "Recipient cannot be blank")
                String recipient,
                @NotNull(message = "Recipient cannot be null")
                @NotBlank(message = "Recipient cannot be blank")
                String sender
        ) {}
}
