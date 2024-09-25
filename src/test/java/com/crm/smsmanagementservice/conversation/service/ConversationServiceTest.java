package com.crm.smsmanagementservice.conversation.service;

import com.crm.smsmanagementservice.conversation.ConversationDTO;
import com.crm.smsmanagementservice.conversation.ConversationStatus;
import com.crm.smsmanagementservice.conversation.persistence.ConversationDocument;
import com.crm.smsmanagementservice.conversation.persistence.ConversationMapper;
import com.crm.smsmanagementservice.conversation.persistence.ConversationRepository;
import com.crm.smsmanagementservice.core.exception.DomainException;
import com.crm.smsmanagementservice.core.exception.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConversationServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ConversationMapper conversationMapper;

    @InjectMocks
    private ConversationService conversationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

   @Test
    void testCreateConversation_Success() {
        ConversationDTO dto = ConversationDTO.builder()
                .userId("user1")
                .contactId("contact1")
                .conversationName("Test Conversation")
                .build();

        // Mock no existing conversation
        when(conversationRepository.findByUserIdAndContactId("user1", "contact1")).thenReturn(Optional.empty());

        // Mock the new conversation creation
        ConversationDocument savedDocument = ConversationDocument.builder()
                .id("conv1")
                .userId("user1")
                .contactId("contact1")
                .conversationName("Test Conversation")
                .createdDate(ZonedDateTime.now())
                .updatedDate(ZonedDateTime.now())
                .status(ConversationStatus.OPEN)
                .build();

        when(conversationMapper.toDocument(any(ConversationDTO.class))).thenReturn(savedDocument);
        when(conversationRepository.save(any(ConversationDocument.class))).thenReturn(savedDocument);
        when(conversationMapper.toDTO(any(ConversationDocument.class))).thenReturn(dto);

        // Test the method
        ConversationDTO result = conversationService.createConversation(dto);

        // Verify
        assertEquals("Test Conversation", result.conversationName());
        verify(conversationRepository, times(1)).findByUserIdAndContactId("user1", "contact1");
        verify(conversationRepository, times(1)).save(any(ConversationDocument.class));
    }

    @Test
    void testCreateConversation_AlreadyExists() {
        ConversationDTO dto = ConversationDTO.builder()
                .userId("user1")
                .contactId("contact1")
                .build();

        // Mock that the conversation already exists
        when(conversationRepository.findByUserIdAndContactId("user1", "contact1"))
                .thenReturn(Optional.of(ConversationDocument.builder().build()));

        // Test for exception
        DomainException exception = assertThrows(DomainException.class, () -> {
            conversationService.createConversation(dto);
        });

        assertEquals(Error.ENTITY_ALREADY_EXISTS.getCode(), exception.getCode());
        verify(conversationRepository, times(1)).findByUserIdAndContactId("user1", "contact1");
        verify(conversationRepository, never()).save(any(ConversationDocument.class));
    }

    @Test
    void testGetConversationById_Success() {
        String conversationId = "conv1";

        ConversationDocument conversationDocument = ConversationDocument.builder()
                .id(conversationId)
                .userId("user1")
                .contactId("contact1")
                .conversationName("Test Conversation")
                .build();

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversationDocument));
        ConversationDTO expectedDto = ConversationDTO.builder()
                .id(conversationId)
                .userId("user1")
                .contactId("contact1")
                .conversationName("Test Conversation")
                .build();
        when(conversationMapper.toDTO(conversationDocument)).thenReturn(expectedDto);

        ConversationDTO result = conversationService.getConversationById(conversationId);

        assertEquals(conversationId, result.id());
        verify(conversationRepository, times(1)).findById(conversationId);
    }

    @Test
    void testGetConversationById_NotFound() {
        String conversationId = "conv1";

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.empty());

        DomainException exception = assertThrows(DomainException.class, () -> {
            conversationService.getConversationById(conversationId);
        });

        assertEquals(Error.ENTITY_NOT_FOUND.getCode(), exception.getCode());
        verify(conversationRepository, times(1)).findById(conversationId);
    }

    @Test
    void testGetConversationsByUserId_Success() {
        String userId = "user1";
        Pageable pageable = PageRequest.of(0, 10);

        ConversationDocument conversationDocument = ConversationDocument.builder()
                .id("conv1")
                .userId("user1")
                .contactId("contact1")
                .conversationName("Test Conversation")
                .build();

        Page<ConversationDocument> page = new PageImpl<>(Collections.singletonList(conversationDocument));
        when(conversationRepository.findAllByUserId(userId, pageable)).thenReturn(page);

        ConversationDTO dto = ConversationDTO.builder()
                .id("conv1")
                .userId("user1")
                .contactId("contact1")
                .conversationName("Test Conversation")
                .build();

        when(conversationMapper.toDTO(conversationDocument)).thenReturn(dto);

        Page<ConversationDTO> result = conversationService.getConversationsByUserId(userId, pageable);

        assertEquals(1, result.getTotalElements());
        verify(conversationRepository, times(1)).findAllByUserId(userId, pageable);
    }

 @Test
    void testUpdateConversation_Success() {
        String conversationId = "conv1";
        ConversationDTO updateDto = ConversationDTO.builder()
                .conversationName("Updated Conversation")
                .status(ConversationStatus.CLOSED)
                .build();

        ConversationDocument existingDocument = ConversationDocument.builder()
                .id(conversationId)
                .conversationName("Test Conversation")
                .status(ConversationStatus.OPEN)
                .build();

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(existingDocument));

        when(conversationRepository.save(any(ConversationDocument.class))).thenReturn(existingDocument);
        when(conversationMapper.toDTO(existingDocument)).thenReturn(updateDto);

        ConversationDTO result = conversationService.updateConversation(conversationId, updateDto);

        assertEquals("Updated Conversation", result.conversationName());
        assertEquals(ConversationStatus.CLOSED, result.status());
        verify(conversationRepository, times(1)).save(any(ConversationDocument.class));
    }

    @Test
    void testUpdateConversation_NotFound() {
        String conversationId = "conv1";
        ConversationDTO updateDto = ConversationDTO.builder().conversationName("Updated Conversation").build();

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.empty());

        DomainException exception = assertThrows(DomainException.class, () -> {
            conversationService.updateConversation(conversationId, updateDto);
        });

        assertEquals(Error.ENTITY_NOT_FOUND.getCode(), exception.getCode());
        verify(conversationRepository, times(1)).findById(conversationId);
        verify(conversationRepository, never()).save(any(ConversationDocument.class));
    }

    @Test
    void testFindOrCreateConversation_ExistingConversation() {
        String userId = "user1";
        String contactId = "contact1";

        // Mock existing conversation
        ConversationDocument existingConversation = ConversationDocument.builder()
                .id("conv1")
                .userId(userId)
                .contactId(contactId)
                .build();

        when(conversationRepository.findByUserIdAndContactId(userId, contactId))
                .thenReturn(Optional.of(existingConversation));

        // Test the method
        String conversationId = conversationService.findOrCreateConversation(userId, contactId);

        // Verify
        assertEquals("conv1", conversationId);
        verify(conversationRepository, times(1)).findByUserIdAndContactId(userId, contactId);
        verify(conversationRepository, never()).save(any());
    }

    @Test
    void testFindOrCreateConversation_NewConversation() {
        String userId = "user1";
        String contactId = "contact1";

        // Mock no existing conversation
        when(conversationRepository.findByUserIdAndContactId(userId, contactId)).thenReturn(Optional.empty());

        // Mock the new conversation creation
        ConversationDocument newConversation = ConversationDocument.builder()
                .id("conv1")
                .userId(userId)
                .contactId(contactId)
                .conversationName(null)
                .createdDate(ZonedDateTime.now())
                .updatedDate(ZonedDateTime.now())
                .status(ConversationStatus.OPEN)
                .build();

        when(conversationMapper.toDocument(any(ConversationDTO.class))).thenReturn(newConversation);
        when(conversationRepository.save(any(ConversationDocument.class))).thenReturn(newConversation);

        // Test the method
        String conversationId = conversationService.findOrCreateConversation(userId, contactId);

        // Verify
        assertEquals("conv1", conversationId);
        verify(conversationRepository, times(1)).findByUserIdAndContactId(userId, contactId);
        verify(conversationRepository, times(1)).save(any(ConversationDocument.class));
    }
}
