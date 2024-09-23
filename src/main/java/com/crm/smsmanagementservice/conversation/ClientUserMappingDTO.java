package com.crm.smsmanagementservice.conversation;

import java.time.ZonedDateTime;
import lombok.Builder;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-05, Friday
 */
@Builder
public record ClientUserMappingDTO(
        String id,
        String userId,
        String contactId,
        String contactNumber,
        String userNumber,
        ZonedDateTime lastUpdated
) {}
