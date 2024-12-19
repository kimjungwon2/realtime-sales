package org.realtime.sales.api;

import lombok.RequiredArgsConstructor;
import org.realtime.sales.service.SalesService;
import org.realtime.sales.service.StoreSalesService;
import org.realtime.sales.service.dto.DeductTerminalSalesRequestDto;
import org.realtime.sales.service.dto.TodaySales;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class SalesApi {

    private final StoreSalesService storeSalesService;
    private final SalesService salesService;

    @GetMapping("/sale/store/{storeId}")
    public List<TodaySales> getStoreSales(@PathVariable("storeId") Long storeId){
        List<String> terminalLists = getTerminalLists(storeId);

        List<TodaySales> todaySalesList = storeSalesService.calculateTodaySalesByPaymentMethod(terminalLists);

        return todaySalesList;
    }

    @PostMapping("/sale/store")
    public void deductTerminalSales(@Validated @RequestBody DeductTerminalSalesRequestDto request){
        String terminalId = request.getTerminalId();
        int amount = request.getAmount();
        String paymentMethod = request.getPaymentMethod();
        String dateKey = request.getDateKey();

        salesService.deductTerminalSales(terminalId, dateKey, paymentMethod, amount);
    }


    private List<String> getTerminalLists(Long storeId) {
        return List.of("12345667","12345668","12345669");
    }



}
