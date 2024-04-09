package com.crm.smsmanagementservice.repository;

import com.crm.smsmanagementservice.entity.MessageDocument;
import com.crm.smsmanagementservice.enums.MessageStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Mongo repository for persisting message documents.
 *
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/18/2024, Sunday
 */
@Repository
public interface MessageRepository extends MongoRepository<MessageDocument, String> {
    /**
     * Find all messages by status.
     * @param statuses list of statuses
     * @return list of message documents
     */
    @Query("{ 'status' : { $in: ?0 } }")
    List<MessageDocument> findAllByStatus(List<MessageStatus> statuses);
}
