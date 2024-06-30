package com.crm.smsmanagementservice.service.message;

import com.crm.smsmanagementservice.dto.request.IMessageStatusCallback;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 6/1/2024, Saturday
 */
public interface IMessageStatusListenerService {
     void onMessageStatusChanged(IMessageStatusCallback messageStatusCallback);
     void pollMessageStatus();
}
