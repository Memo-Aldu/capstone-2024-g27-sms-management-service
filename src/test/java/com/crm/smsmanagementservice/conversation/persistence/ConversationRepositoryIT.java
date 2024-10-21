package com.crm.smsmanagementservice.conversation.persistence;

import com.crm.smsmanagementservice.config.EmbeddedMongoConfig;
import com.crm.smsmanagementservice.conversation.ConversationStatus;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(EmbeddedMongoConfig.class) // Ensure the EmbeddedMongoConfig is imported
public class ConversationRepositoryIT {

    @Autowired
    private ConversationRepository conversationRepository;

    @BeforeEach
    public void setUp() {
        conversationRepository.deleteAll();

        ConversationDocument conversation1 = ConversationDocument.builder()
                .id("conv-1")
                .userId("user-1")
                .contactId("contact-1")
                .conversationName("Test Conversation 1")
                .status(ConversationStatus.OPEN)
                .createdDate(ZonedDateTime.now())
                .updatedDate(ZonedDateTime.now())
                .build();

        ConversationDocument conversation2 = ConversationDocument.builder()
                .id("conv-2")
                .userId("user-2")
                .contactId("contact-2")
                .conversationName("Test Conversation 2")
                .status(ConversationStatus.OPEN)
                .createdDate(ZonedDateTime.now())
                .updatedDate(ZonedDateTime.now())
                .build();

        conversationRepository.save(conversation1);
        conversationRepository.save(conversation2);
    }

    @Test
    public void testFindAllByUserId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ConversationDocument> page = conversationRepository.findAllByUserId("user-1", pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals("user-1", page.getContent().get(0).getUserId());
    }

    @Test
    public void testFindByUserIdAndContactId() {
        Optional<ConversationDocument> result = conversationRepository.findByUserIdAndContactId("user-1", "contact-1");

        assertTrue(result.isPresent());
        assertEquals("user-1", result.get().getUserId());
        assertEquals("contact-1", result.get().getContactId());
    }

    @Test
    public void testFindByUserIdAndContactIdNotFound() {
        Optional<ConversationDocument> result = conversationRepository.findByUserIdAndContactId("user-1", "contact-99");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testPagination() {
        Pageable pageable = PageRequest.of(0, 1);

        Page<ConversationDocument> page = conversationRepository.findAllByUserId("user-1", pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        assertEquals("user-1", page.getContent().get(0).getUserId());
    }
}