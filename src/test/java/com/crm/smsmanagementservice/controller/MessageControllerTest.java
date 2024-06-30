package com.crm.smsmanagementservice.controller;

import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.enums.MessageStatus;
import com.crm.smsmanagementservice.service.message.IMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 6/10/2024, Monday
 */
public class MessageControllerTest {
  @InjectMocks private MessageController messageController;

  @Mock private IMessageService messageService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void shouldGetConversationByIdSuccessfully() {
    String conversationId = "CONVERSATION_ID";
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdTime"));
    MessageResponseDto responseDto =  new MessageResponseDto("MESSAGE_ID", "SENDER",
                "CONVERSATION_ID", MessageStatus.DELIVERED,
                    ZonedDateTime.now(), ZonedDateTime.now(), null);
    Page<MessageResponseDto> page = new PageImpl<>(
            Collections.singletonList(responseDto), pageable, 1
        );
    when(messageService.getMessagesByConversationId(conversationId, pageable)).thenReturn(page);

    ResponseEntity<Page<MessageResponseDto>> response = messageController.getMessagesByConversation(
            conversationId, 0, 10, "createdTime", "asc");

    assert response.getBody() != null;
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(page, response.getBody());
  }
}
