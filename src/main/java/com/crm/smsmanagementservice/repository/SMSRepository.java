package com.crm.smsmanagementservice.repository;

import com.crm.smsmanagementservice.entity.SmSDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/18/2024, Sunday
 */
@Repository
public interface SMSRepository extends MongoRepository<SmSDocument, String> {

}
