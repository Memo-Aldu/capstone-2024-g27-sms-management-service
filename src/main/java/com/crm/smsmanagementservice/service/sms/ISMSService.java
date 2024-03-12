package com.crm.smsmanagementservice.service.sms;

import com.crm.smsmanagementservice.dto.request.*;
import com.crm.smsmanagementservice.dto.response.SMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.SMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.SMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.SMSSendResponseDto;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2/18/2024, Sunday
 */
public interface ISMSService {

    SMSSendResponseDto sendSMS(SMSSendRequestDto request);
    SMSScheduleResponseDto scheduleSMS(SMSScheduleRequestDto request);
    SMSBulkSendResponseDto sendBulkSMS(SMSBulkSendRequestDto request);
    SMSBulkScheduleResponseDto scheduleBulkSMS(SMSBulkScheduleRequestDto request);
    void updateSMSStatus(IMessageStatusCallback smsStatus);
    void pollSMSStatus();
}