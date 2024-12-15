package org.realtime.sales.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class TodaySales {
    private Integer totalSales;
    private PaymentMethod method;

    @Builder
    public TodaySales(Integer totalSales, PaymentMethod method) {
        this.totalSales = totalSales;
        this.method = method;
    }
}
