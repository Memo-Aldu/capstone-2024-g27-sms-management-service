package com.crm.smsmanagementservice.repository;

import com.crm.smsmanagementservice.entity.ConversationDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 5/18/2024, Saturday
 */
@Repository
public interface ConversationRepository extends MongoRepository<ConversationDocument, String> {
    @Query("{$or: [{sender: ?0}, {recipient: ?0}]}")
    Page<ConversationDocument> findConversationDocumentByRecipientOrSender(String participant, Pageable pageable);
    Optional<ConversationDocument> findConversationDocumentBySenderAndRecipient(String sender, String recipient);
}
