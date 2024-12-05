package com.crm.smsmanagementservice.message.web;

import com.crm.smsmanagementservice.core.dto.DomainAPIResponse;
import com.crm.smsmanagementservice.core.util.PageableHelper;
import com.crm.smsmanagementservice.message.MessageDTO;
import com.crm.smsmanagementservice.message.MessageExternalAPI;
import com.crm.smsmanagementservice.message.MessageType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-06, Saturday
 */
@RestController @RequestMapping("/api/v1/messages")
@RequiredArgsConstructor @Slf4j(topic = "MESSAGE_CONTROLLER")
public class MessageController {
    private final MessageExternalAPI messageService;

    @GetMapping("/{id}")
    public ResponseEntity<DomainAPIResponse<MessageDTO>> getMessageById(@PathVariable String id) {
        MessageDTO response = messageService.getMessageById(id);
        DomainAPIResponse<MessageDTO> DomainAPIResponse =
                com.crm.smsmanagementservice.core.dto.DomainAPIResponse.<MessageDTO>builder()
                .responseStatus(com.crm.smsmanagementservice.core.dto.DomainAPIResponse.DomainAPIResponseStatus.SUCCESS)
                .status(HttpStatus.OK)
                .data(response)
                .message("Message fetched successfully")
                .build();
        return ResponseEntity.ok(DomainAPIResponse);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<DomainAPIResponse<List<MessageDTO>>> getMessagesByUserId(
            @PathVariable String id,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(defaultValue = "createdTime", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String order) {
        Pageable pageable = PageableHelper.createPage(page, size, sortBy, order);
        Page<MessageDTO> response = messageService.getMessagesByUserId(id, pageable);
        DomainAPIResponse<List<MessageDTO>> domainAPIResponse =
                com.crm.smsmanagementservice.core.dto.DomainAPIResponse.<List<MessageDTO>>builder()
                        .responseStatus(com.crm.smsmanagementservice.core.dto.DomainAPIResponse.DomainAPIResponseStatus.SUCCESS)
                        .status(HttpStatus.OK)
                        .data(response.getContent())
                        .currentPage(response.getNumber())
                        .totalPages(response.getTotalPages())
                        .totalElements(response.getTotalElements())
                        .message("Messages fetched successfully")
                .build();
        return ResponseEntity.ok(domainAPIResponse);
    }

    @GetMapping
    public ResponseEntity<DomainAPIResponse<List<MessageDTO>>> getMessages(
            @RequestParam String userId,
            @RequestParam String contactId,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(defaultValue = "createdTime", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String order) {
        Pageable pageable = PageableHelper.createPage(page, size, sortBy, order);
        Page<MessageDTO> response = messageService.getMessageByParticipantId(userId, contactId, pageable);
        DomainAPIResponse<List<MessageDTO>> domainAPIResponse =
                com.crm.smsmanagementservice.core.dto.DomainAPIResponse.<List<MessageDTO>>builder()
                .responseStatus(com.crm.smsmanagementservice.core.dto.DomainAPIResponse.DomainAPIResponseStatus.SUCCESS)
                .status(HttpStatus.OK)
                .data(response.getContent())
                .currentPage(response.getNumber())
                .totalPages(response.getTotalPages())
                .totalElements(response.getTotalElements())
                .message("Messages fetched successfully")
                .build();
        return ResponseEntity.ok(domainAPIResponse);
    }

    @PostMapping
    public ResponseEntity<DomainAPIResponse<List<MessageDTO>>> createMessage(@Valid @RequestBody MessageCreateDTO messageCreateDTO) {
        MessageType messageType = messageCreateDTO.media() == null ||
                messageCreateDTO.media().isEmpty() ? MessageType.SMS : MessageType.MMS;
        List<MessageDTO> messageDTOS = messageCreateDTO.messageItems()
                .stream()
                .map(messageItem -> {
                    return MessageDTO.builder()
                            .contactId(messageItem.contactId())
                            .userId(messageCreateDTO.userId())
                            .to(messageItem.to())
                            .from(messageCreateDTO.from())
                            .content(messageItem.content())
                            .type(messageType)
                            .scheduledDate(messageCreateDTO.scheduledDate())
                            .media(messageCreateDTO.media())
                            .build();
                })
                .toList();

        List<MessageDTO> response = messageService.createMessage(messageDTOS);
        DomainAPIResponse<List<MessageDTO>> domainAPIResponse =
                com.crm.smsmanagementservice.core.dto.DomainAPIResponse.<List<MessageDTO>>builder()
                .responseStatus(com.crm.smsmanagementservice.core.dto.DomainAPIResponse.DomainAPIResponseStatus.SUCCESS)
                .status(HttpStatus.CREATED)
                .data(response)
                .message("Messages created successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(domainAPIResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DomainAPIResponse<MessageDTO>> updateMessage(@PathVariable String id) {
        MessageDTO response = messageService.cancelMessage(id);
        DomainAPIResponse<MessageDTO> domainAPIResponse =
                com.crm.smsmanagementservice.core.dto.DomainAPIResponse.<MessageDTO>builder()
                .responseStatus(com.crm.smsmanagementservice.core.dto.DomainAPIResponse.DomainAPIResponseStatus.SUCCESS)
                .status(HttpStatus.OK)
                .data(response)
                .message("Message Canceled successfully")
                .build();
        return ResponseEntity.ok(domainAPIResponse);
    }
}
