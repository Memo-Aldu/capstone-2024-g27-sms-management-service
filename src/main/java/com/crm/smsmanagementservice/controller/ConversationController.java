package com.crm.smsmanagementservice.controller;

import com.crm.smsmanagementservice.dto.request.conversation.ConversationPatchRequestDto;
import com.crm.smsmanagementservice.dto.request.conversation.ConversationPostRequestDto;
import com.crm.smsmanagementservice.dto.response.conversation.ConversationResponseDto;
import com.crm.smsmanagementservice.service.conversation.IConversationService;
import com.crm.smsmanagementservice.util.helper.PageableHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

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
    private final IConversationService conversationService;

    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponseDto> getConversationById(@PathVariable String id) {
        log.info("Fetching conversation with id: {}", id);
        ConversationResponseDto response = conversationService.getConversationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/participant/{participant}")
    public ResponseEntity<Page<ConversationResponseDto>> getConversationsByParticipant(
            @PathVariable String participant,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(defaultValue = "updatedTime", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String order) {
        log.info("Fetching conversations by participant: {}", participant);
        Pageable pageable = PageableHelper.createPage(page, size, sortBy, order);
        Page<ConversationResponseDto> response =
                conversationService.getConversationsByParticipant(participant, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ConversationResponseDto> createConversation(
            @Valid @RequestBody ConversationPostRequestDto requestDto) throws URISyntaxException {
        log.info("Creating conversation with name: {}", requestDto.conversationName());
        ConversationResponseDto response = conversationService.createConversation(requestDto);
        return ResponseEntity.created(new URI("/api/v1/conversation/" + response.id())).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ConversationResponseDto> updateConversation(
            @PathVariable String id, @Valid @RequestBody ConversationPatchRequestDto requestDto) {
        log.info("Updating conversation with id: {}", id);
        ConversationResponseDto response = conversationService.updateConversation(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteConversation(@PathVariable String id) {
        log.info("Deleting conversation with id: {}", id);
        conversationService.deleteConversation(id);
        return ResponseEntity.noContent().build();
    }
}
