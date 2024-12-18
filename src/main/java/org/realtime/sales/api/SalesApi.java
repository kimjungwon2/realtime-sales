package org.realtime.sales.api;

import lombok.RequiredArgsConstructor;
import org.realtime.sales.service.StoreSalesService;
import org.realtime.sales.service.dto.TodaySales;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class SalesApi {

    private final StoreSalesService storeSalesService;

    @GetMapping("/sale/store/{storeId}")
    public List<TodaySales> getStoreSales(@PathVariable("storeId") Long storeId){
        List<String> terminalLists = getTerminalLists(storeId);

        List<TodaySales> todaySalesList = storeSalesService.calculateTodaySalesByPaymentMethod(terminalLists);

        return todaySalesList;
    }

    private List<String> getTerminalLists(Long storeId) {
        return List.of("12345667","12345668","12345669");
    }
}
