package org.realtime.sales.service;

import org.springframework.stereotype.Service;
import java.util.*;


@Service
public class StoreSalesService {
    private final SalesManageService salesManageService;

    public StoreSalesService(SalesManageService salesManageService) {
        this.salesManageService = salesManageService;
    }

    public Integer getTodayTotalSales(List<String> terminalIds, String method){
        Integer todaySales = 0;

        for(String terminalId : terminalIds){
            todaySales += salesManageService.getSalesValue(terminalId, method);
        }

        return todaySales;
    }

}
