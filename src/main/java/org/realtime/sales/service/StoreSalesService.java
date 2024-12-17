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

    public List<TodaySales> calculateTodaySalesByPaymentMethod(List<String> terminalIds){

        List<TodaySales> todaySalesList = new ArrayList<>();

        List<PaymentMethod> paymentMethods = new ArrayList<>();

        paymentMethods.add(PaymentMethod.CARD);
        paymentMethods.add(PaymentMethod.POINT);
        paymentMethods.add(PaymentMethod.CASH);

        for(PaymentMethod paymentMethod : paymentMethods){
            todaySalesList.add(getTodayTotalSales(terminalIds,paymentMethod));
        }

        return todaySalesList;
    }


    private TodaySales getTodayTotalSales(List<String> terminalIds,PaymentMethod paymentMethod){
        Integer todaySales = 0;

        for (String terminalId : terminalIds) {
            todaySales += salesManageService.getSalesValue(terminalId, paymentMethod);
        }

        return TodaySales.builder()
                    .method(paymentMethod)
                    .totalSales(todaySales)
                    .build();
    }

}
