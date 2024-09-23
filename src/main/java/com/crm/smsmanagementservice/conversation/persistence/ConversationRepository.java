package com.crm.smsmanagementservice.conversation.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 5/18/2024, Saturday
 */
@Repository
public interface ConversationRepository extends MongoRepository<ConversationDocument, String> {
    Page<ConversationDocument> findAllByUserId (String userId, Pageable pageable);
    Optional<ConversationDocument> findByUserIdAndContactId(String userId, String contactId);
}
