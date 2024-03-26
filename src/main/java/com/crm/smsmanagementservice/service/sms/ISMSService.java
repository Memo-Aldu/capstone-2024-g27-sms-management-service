package com.crm.smsmanagementservice.service.sms;

import com.crm.smsmanagementservice.dto.request.*;
import com.crm.smsmanagementservice.dto.request.sms.SMSBulkScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSBulkSendRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.sms.SMSSendRequestDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.sms.SMSSendResponseDto;

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