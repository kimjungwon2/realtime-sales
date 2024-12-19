package org.realtime.sales.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class DeductTerminalSalesRequestDto {
    private String terminalId;
    private String dateKey;
    private String paymentMethod;
    private int amount;

}
