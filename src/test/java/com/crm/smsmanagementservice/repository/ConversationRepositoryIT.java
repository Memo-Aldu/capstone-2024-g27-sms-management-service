package com.crm.smsmanagementservice.repository;

import com.crm.smsmanagementservice.config.EmbeddedMongoConfig;
import com.crm.smsmanagementservice.entity.ConversationDocument;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.time.ZonedDateTime;


/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 6/3/2024, Monday
 */
@DataMongoTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class) @TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(EmbeddedMongoConfig.class) @RequiredArgsConstructor
public class ConversationRepositoryIT {

    @Autowired
    private ConversationRepository conversationRepository;


    @BeforeEach
    public void setup() {
        conversationRepository.deleteAll();
    }

    @Test
    public void whenSaveConversation_thenSuccess() {
        ConversationDocument document = ConversationDocument.builder()
                .id("CONVERSATION_ID")
                .createdTime(ZonedDateTime.now())
                .updatedTime(ZonedDateTime.now())
                .build();
        conversationRepository.save(document);
        ConversationDocument savedDocument = conversationRepository.findById("CONVERSATION_ID").get();
        Assertions.assertEquals(document.getId(), savedDocument.getId());
    }

    @Test
    public void whenFindConversationDocumentByRecipientOrSender_thenSuccess() {
        ConversationDocument document = ConversationDocument.builder()
                .id("CONVERSATION_ID")
                .createdTime(ZonedDateTime.now())
                .updatedTime(ZonedDateTime.now())
                .sender("SENDER")
                .recipient("RECIPIENT")
                .build();
        conversationRepository.save(document);
        ConversationDocument savedDocument = conversationRepository
                .findConversationDocumentByRecipientOrSender("SENDER", null).getContent().getFirst();
        Assertions.assertEquals(document.getId(), savedDocument.getId());
    }


    @Test
    public void whenFindConversationDocumentBySenderAndRecipient_thenSuccess() {
        ConversationDocument document = ConversationDocument.builder()
                .id("CONVERSATION_ID")
                .createdTime(ZonedDateTime.now())
                .updatedTime(ZonedDateTime.now())
                .sender("SENDER")
                .recipient("RECIPIENT")
                .build();
        conversationRepository.save(document);
        ConversationDocument savedDocument = conversationRepository
                .findConversationDocumentBySenderAndRecipient("SENDER", "RECIPIENT").orElse(
                        ConversationDocument.builder().build());
        Assertions.assertEquals(document.getId(), savedDocument.getId());
    }
}
