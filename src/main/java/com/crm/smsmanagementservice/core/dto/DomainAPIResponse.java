package com.crm.smsmanagementservice.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.http.HttpStatus;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 2024-07-05, Friday
 */
@Builder @JsonInclude(JsonInclude.Include.NON_NULL)
public record DomainAPIResponse<T>(
        @JsonProperty("httpStatus")
        HttpStatus status,
        @JsonProperty("responseStatus")
        DomainAPIResponseStatus responseStatus,
        @JsonProperty("responseMessage")
        String message,
        @JsonProperty("data")
        T data,
        @JsonProperty("error")
        T error,
        @JsonProperty("currentPage")
        Integer currentPage,
        @JsonProperty("totalPages")
        Integer totalPages,
        @JsonProperty("totalElements")
        Long totalElements
) {
        public enum DomainAPIResponseStatus {
                SUCCESS,
                ERROR
        }
}
