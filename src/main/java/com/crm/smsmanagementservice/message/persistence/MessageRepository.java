package com.crm.smsmanagementservice.message.persistence;


import com.crm.smsmanagementservice.core.enums.MessageDirection;
import com.crm.smsmanagementservice.core.enums.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Mongo repository for persisting message documents.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/18/2024, Sunday
 */
@Repository
public interface MessageRepository extends MongoRepository<MessageDocument, String> {
    Page<MessageDocument> findByUserIdAndContactIdAndStatus(String userId, String contactId, MessageStatus status, Pageable pageable);
    Optional<MessageDocument> findByResourceId(String resourceId);
    Optional<MessageDocument> findFirstByToAndDirectionOrderByCreatedDateDesc(String to, MessageDirection direction);
}
