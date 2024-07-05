package com.crm.smsmanagementservice.service;

import com.crm.smsmanagementservice.dto.request.IMessageStatusCallback;
import com.crm.smsmanagementservice.dto.response.message.MessageResponseDto;
import com.crm.smsmanagementservice.enums.MessageStatus;
import com.crm.smsmanagementservice.service.message.IMessageService;
import com.crm.smsmanagementservice.service.message.MessageStatusListenerService;
import com.crm.smsmanagementservice.service.provider.IMessageWrapper;
import com.crm.smsmanagementservice.service.provider.IMessagingProviderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 6/23/2024, Sunday
 */
@ExtendWith(MockitoExtension.class)
public class MessageStatusListenerServiceTest {
    @Mock private IMessageService messageService;
    @Mock private IMessagingProviderService messageProviderService;
    @InjectMocks private MessageStatusListenerService messageStatusListenerService;

    @Test
    void onMessageStatusChanged_thenSuccess() {
        // given
        var statusCallback = mock(IMessageStatusCallback.class);
        // when
        when(statusCallback.getMessageId()).thenReturn("MESSAGE_ID");
        when(statusCallback.getStatus()).thenReturn("SENT");
        when(statusCallback.getErrorCode()).thenReturn(Optional.empty());
        when(statusCallback.getErrorMessage()).thenReturn(Optional.empty());
        messageStatusListenerService.onMessageStatusChanged(statusCallback);
        // then
        verify(messageService, times(1))
                .updateMessageStatus("MESSAGE_ID", MessageStatus.SENT, null, null);
        verify(messageService, times(1))
                .updateMessageStatus(
                        statusCallback.getMessageId(),
                        MessageStatus.fromString(statusCallback.getStatus()),
                        statusCallback.getErrorCode().orElse(null),
                        statusCallback.getErrorMessage().orElse(null));
    }

    @Test
    void pollMessageStatus_thenSuccess() {
        // given
        List<MessageResponseDto> messageResponseDtos = List.of(
                MessageResponseDto.builder()
                        .id("MESSAGE_ID_1")
                        .status(MessageStatus.QUEUED)
                        .build(),
                MessageResponseDto.builder()
                        .id("MESSAGE_ID_2")
                        .status(MessageStatus.QUEUED)
                        .build()
        );
        IMessageWrapper messageWrapper1 = mock(IMessageWrapper.class);
        IMessageWrapper messageWrapper2 = mock(IMessageWrapper.class);

        // when
        when(messageWrapper1.getStatus()).thenReturn(MessageStatus.SENT);
        when(messageWrapper1.getId()).thenReturn("MESSAGE_ID_1");

        when(messageWrapper2.getStatus()).thenReturn(MessageStatus.QUEUED);

        when(messageService.getAllMessagesByStatus(any())).thenReturn(messageResponseDtos);
        when(messageProviderService.fetchMessageById("MESSAGE_ID_1")).thenReturn(messageWrapper1);
        when(messageProviderService.fetchMessageById("MESSAGE_ID_2")).thenReturn(messageWrapper2);
        when(messageProviderService.pollMessageStatus()).thenReturn(true);
        when(messageService.getAllMessagesByStatus(any())).thenReturn(messageResponseDtos);
        messageStatusListenerService.pollMessageStatus();
        // then
        verify(messageService, times(1)).updateMessageStatus
                ("MESSAGE_ID_1", MessageStatus.SENT, null, null);
    }
}
