package com.crm.smsmanagementservice.controller;

import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.service.message.IMessageService;
import com.crm.smsmanagementservice.util.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 6/1/2024, Saturday
 */
@RequestMapping("/api/v1/messages") @RestController
@RequiredArgsConstructor @Slf4j(topic = "MESSAGE_CONTROLLER")
public class MessageController {
    private final IMessageService messageService;

    // Get messages by conversation request variable
    @GetMapping
    public ResponseEntity<Page<MessageResponseDto>> getMessagesByConversation(
            @RequestParam String conversationId,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(defaultValue = "createdTime", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String order) {
        log.info("Fetching messages by conversation: {}", conversationId);
        Pageable pageable = PageableHelper.createPage(page, size, sortBy, order);
        Page<MessageResponseDto> response = messageService.getMessagesByConversationId(conversationId, pageable);
        return ResponseEntity.ok(response);
    }
}
