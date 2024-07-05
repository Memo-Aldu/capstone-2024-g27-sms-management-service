package com.crm.smsmanagementservice.repository;

import com.crm.smsmanagementservice.config.EmbeddedMongoConfig;
import com.crm.smsmanagementservice.entity.MessageDocument;
import com.crm.smsmanagementservice.enums.MessageStatus;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 6/8/2024, Saturday
 */
@DataMongoTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class) @TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(EmbeddedMongoConfig.class) @RequiredArgsConstructor
public class MessageRepositoryIT {
    @Autowired
    private MessageRepository messageRepository;

    @BeforeEach
    public void setup() {
        messageRepository.deleteAll();
    }

    @Test
    public void whenSaveMessage_thenSuccess() {
        MessageDocument document = MessageDocument.builder()
                .id("MESSAGE_ID")
                .createdTime(ZonedDateTime.now()).messageContent("Hello, how are you?")
                .sender("SENDER").recipient("RECIPIENT").status(MessageStatus.SENT)
                .conversationId("CONVERSATION_ID")
                .build();
        messageRepository.save(document);
        MessageDocument savedDocument = messageRepository.findById("MESSAGE_ID").orElse(null);
        assert savedDocument != null;
        Assertions.assertEquals(document.getId(), savedDocument.getId());
    }

    @Test
    public void whenFindAllByStatus_thenSuccess() {
        MessageDocument document = MessageDocument.builder()
                .id("MESSAGE_ID")
                .createdTime(ZonedDateTime.now()).messageContent("Hello, how are you?")
                .sender("SENDER").recipient("RECIPIENT").status(MessageStatus.SENT)
                .conversationId("CONVERSATION_ID")
                .build();
        messageRepository.save(document);
        MessageDocument savedDocument = messageRepository.findAllByStatus(List.of(MessageStatus.SENT)).getFirst();
        assert savedDocument != null;
        Assertions.assertEquals(document.getId(), savedDocument.getId());
    }

    @Test
    public void whenFindByConversationId_thenSuccess() {
        MessageDocument document = MessageDocument.builder()
                .id("MESSAGE_ID")
                .createdTime(ZonedDateTime.now()).messageContent("Hello, how are you?")
                .sender("SENDER").recipient("RECIPIENT").status(MessageStatus.SENT)
                .conversationId("CONVERSATION_ID")
                .build();
        messageRepository.save(document);
        MessageDocument savedDocument = messageRepository.findByConversationId("CONVERSATION_ID", null).getContent().getFirst();
        assert savedDocument != null;
        Assertions.assertEquals(document.getId(), savedDocument.getId());
    }

}
