package com.crm.smsmanagementservice.conversation.web;


import com.crm.smsmanagementservice.conversation.ConversationDTO;
import com.crm.smsmanagementservice.conversation.ConversationExternalAPI;
import com.crm.smsmanagementservice.core.dto.DomainAPIResponse;
import com.crm.smsmanagementservice.core.exception.DomainException;
import com.crm.smsmanagementservice.core.exception.Error;
import com.crm.smsmanagementservice.core.util.PageableHelper;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 5/18/2024, Saturday
 */
@RequestMapping("/api/v1/conversation")
@RestController
@RequiredArgsConstructor
@Slf4j(topic = "CONVERSATION_CONTROLLER")
public class ConversationController {
    private final ConversationExternalAPI conversationService;

    @GetMapping("/{id}")
    public ResponseEntity<DomainAPIResponse<ConversationDTO>> getConversationById(@PathVariable String id) {
        ConversationDTO response = conversationService.getConversationById(id);
        DomainAPIResponse<ConversationDTO> domainAPIResponse =
                com.crm.smsmanagementservice.core.dto.DomainAPIResponse.<ConversationDTO>builder()
                .responseStatus(com.crm.smsmanagementservice.core.dto.DomainAPIResponse.DomainAPIResponseStatus.SUCCESS)
                .status(HttpStatus.OK)
                .data(response)
                .message("Conversation fetched successfully")
                .build();
        return ResponseEntity.ok(domainAPIResponse);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<DomainAPIResponse<List<ConversationDTO>>> getConversationsByParticipant(
            @PathVariable String id,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(defaultValue = "updatedTime", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String order) {
        Pageable pageable = PageableHelper.createPage(page, size, sortBy, order);
        Page<ConversationDTO> response =
                conversationService.getConversationsByUserId(id, pageable);
        DomainAPIResponse<List<ConversationDTO>> domainAPIResponse =
                com.crm.smsmanagementservice.core.dto.DomainAPIResponse.<List<ConversationDTO>>builder()
                .responseStatus(com.crm.smsmanagementservice.core.dto.DomainAPIResponse.DomainAPIResponseStatus.SUCCESS)
                .status(HttpStatus.OK)
                .data(response.getContent())
                .message("Conversations fetched successfully")
                .totalElements(response.getTotalElements())
                .totalPages(response.getTotalPages())
                .currentPage(response.getNumber())
                .build();
        return ResponseEntity.ok(domainAPIResponse);
    }

    @PostMapping
    public ResponseEntity<DomainAPIResponse<ConversationDTO>> createConversation(
            @Valid @RequestBody ConversationCreateDTO requestDto) throws URISyntaxException {
    ConversationDTO response = conversationService.createConversation(
                ConversationDTO.builder().conversationName(requestDto.conversationName())
                    .contactId(requestDto.contactId()).userId(requestDto.userId()).build());
    DomainAPIResponse<ConversationDTO> domainAPIResponse =
            com.crm.smsmanagementservice.core.dto.DomainAPIResponse.<ConversationDTO>builder()
            .responseStatus(com.crm.smsmanagementservice.core.dto.DomainAPIResponse.DomainAPIResponseStatus.SUCCESS)
            .status(HttpStatus.CREATED)
            .data(response)
            .message("Conversation created successfully")
            .build();
        return ResponseEntity.created(new URI("/api/v1/conversation/" + response.id())).body(domainAPIResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DomainAPIResponse<ConversationDTO>> updateConversation(
            @PathVariable String id, @Valid @RequestBody ConversationUpdateDTO requestDto) {
        if (requestDto == null || (requestDto.conversationName() == null && requestDto.status() == null)) {
            throw new DomainException(Error.INVALID_REQUEST);
        }
        ConversationDTO response = conversationService.updateConversation(id,
                ConversationDTO.builder().conversationName(requestDto.conversationName())
                        .status(requestDto.status()).build());
        DomainAPIResponse<ConversationDTO> domainAPIResponse =
                com.crm.smsmanagementservice.core.dto.DomainAPIResponse.<ConversationDTO>builder()
                .responseStatus(com.crm.smsmanagementservice.core.dto.DomainAPIResponse.DomainAPIResponseStatus.SUCCESS)
                .status(HttpStatus.OK)
                .data(response)
                .message("Conversation updated successfully")
                .build();
        return ResponseEntity.ok(domainAPIResponse);
    }
}
