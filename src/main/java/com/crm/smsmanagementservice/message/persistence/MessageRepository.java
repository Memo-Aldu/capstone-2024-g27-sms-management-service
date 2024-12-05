package com.crm.smsmanagementservice.message.persistence;


import com.crm.smsmanagementservice.core.enums.MessageDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
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
    @Query("{ 'userId' : ?0, 'contactId' : ?1, 'status' : { $in: [ 'DELIVERED', 'RECEIVED' ] } }")
    Page<MessageDocument> findDeliveredMessagesByUserIdAndContactId(String userId, String contactId, Pageable pageable);
    Page<MessageDocument> findMessageDocumentByUserId(String userId, Pageable pageable);
    Optional<MessageDocument> findByResourceId(String resourceId);
    Optional<MessageDocument> findFirstByToAndDirectionOrderByCreatedDateDesc(String to, MessageDirection direction);
}
