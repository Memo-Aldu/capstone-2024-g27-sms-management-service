package com.crm.smsmanagementservice.message.persistence;

import com.crm.smsmanagementservice.config.EmbeddedMongoConfig;
import com.crm.smsmanagementservice.conversation.persistence.ConversationRepository;
import com.crm.smsmanagementservice.core.enums.MessageDirection;
import com.crm.smsmanagementservice.core.enums.MessageStatus;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-09-26, Thursday
 */
@DataMongoTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class) @TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(EmbeddedMongoConfig.class) @RequiredArgsConstructor
public class MessageRepositoryIT {
    @Autowired
    private MessageRepository messageRepository;

    private MessageDocument testMessage;
    @Autowired
    private ConversationRepository conversationRepository;

    @BeforeEach
    void setUp() {
        // Initialize and save a test message document
        conversationRepository.deleteAll();
        testMessage = MessageDocument.builder()
                .id("msg-1")
                .resourceId("res-1")
                .userId("user-1")
                .contactId("contact-1")
                .from("+1234567890")
                .to("+0987654321")
                .status(MessageStatus.RECEIVED)
                .direction(MessageDirection.INBOUND)
                .createdDate(ZonedDateTime.now())
                .build();

        messageRepository.save(testMessage);
    }

    @Test
    void testFindDeliveredMessagesByUserIdAndContactId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MessageDocument> result = messageRepository.findDeliveredMessagesByUserIdAndContactId(
                "user-1", "contact-1", pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(testMessage.getId(), result.getContent().getFirst().getId());
    }

    @Test
    void testFindByResourceId() {
        Optional<MessageDocument> result = messageRepository.findByResourceId("res-1");

        assertTrue(result.isPresent());
        assertEquals(testMessage.getId(), result.get().getId());
    }

    @Test
    void testFindFirstByToAndDirectionOrderByCreatedDateDesc() {
        Optional<MessageDocument> result = messageRepository.findFirstByToAndDirectionOrderByCreatedDateDesc("+0987654321", MessageDirection.INBOUND);

        assertTrue(result.isPresent());
        assertEquals(testMessage.getId(), result.get().getId());
    }
}
