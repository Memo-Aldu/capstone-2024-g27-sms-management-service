package com.crm.smsmanagementservice.repository;

import com.crm.smsmanagementservice.entity.SmSDocument;
import com.crm.smsmanagementservice.enums.MessageStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/18/2024, Sunday
 */
@Repository
public interface SMSRepository extends MongoRepository<SmSDocument, String> {
    @Query("{ 'status' : { $in: ?0 } }")
    List<SmSDocument> findAllByStatus(List<MessageStatus> statuses);
}
