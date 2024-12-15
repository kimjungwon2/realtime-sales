package org.realtime.sales.service;

import org.realtime.sales.service.dto.PaymentMethod;
import org.realtime.sales.service.dto.TodaySales;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
public class StoreSalesService {
    private final SalesManageService salesManageService;

    public StoreSalesService(SalesManageService salesManageService) {
        this.salesManageService = salesManageService;
    }

    public TodaySales getTodayTotalSales(List<String> terminalIds,PaymentMethod paymentMethod){
        Integer todaySales = 0;

        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(PaymentMethod.POINT);
        paymentMethods.add(PaymentMethod.CASH);
        paymentMethods.add(PaymentMethod.CARD);


        for (String terminalId : terminalIds) {
            todaySales += salesManageService.getSalesValue(terminalId, paymentMethod);
        }

        return TodaySales.builder()
                    .method(paymentMethod)
                    .totalSales(todaySales)
                    .build();
    }

}
