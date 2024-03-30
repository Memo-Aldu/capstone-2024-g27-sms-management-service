package com.crm.smsmanagementservice.service.mms;

import com.crm.smsmanagementservice.dto.request.mms.MMSBulkScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSBulkSendRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSScheduleRequestDto;
import com.crm.smsmanagementservice.dto.request.mms.MMSSendRequestDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSBulkScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSBulkSendResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSScheduleResponseDto;
import com.crm.smsmanagementservice.dto.response.mms.MMSSendResponseDto;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 3/26/2024, Tuesday
 */
public interface IMMSService {
    MMSSendResponseDto sendMMS(MMSSendRequestDto mmsSendRequest);
    MMSScheduleResponseDto scheduleMMS(MMSScheduleRequestDto mmsScheduleRequest);
    MMSBulkSendResponseDto sendBulkMMS(MMSBulkSendRequestDto mmsBulkRequest);
    MMSBulkScheduleResponseDto scheduleBulkMMS(MMSBulkScheduleRequestDto mmsBulkScheduleRequest);
}
