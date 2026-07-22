package com.sashimi.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WsTicketResponseDto {

    private String wsTicket;
    private long expiresIn;

    @Builder
    public WsTicketResponseDto(String wsTicket, long expiresIn) {
        this.wsTicket = wsTicket;
        this.expiresIn = expiresIn;
    }
}
