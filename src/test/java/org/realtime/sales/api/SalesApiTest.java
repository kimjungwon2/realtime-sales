package org.realtime.sales.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.realtime.sales.service.StoreSalesService;
import org.realtime.sales.service.dto.PaymentMethod;
import org.realtime.sales.service.dto.TodaySales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@WebMvcTest(SalesApi.class)
class SalesApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreSalesService storeSalesService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void getStoreSales() {
        // Given
        Long storeId = 1L;
        List<String> mockTerminalLists = List.of("12345667", "12345668", "12345669");
        List<TodaySales> mockTodaySalesList = List.of(
                new TodaySales(50000, PaymentMethod.CARD),
                new TodaySales(20000, PaymentMethod.CASH)
        );

        when(storeSalesService.calculateTodaySalesByPaymentMethod(mockTerminalLists))
                .thenReturn(mockTodaySalesList);

        // When & Then
        try {
            mockMvc.perform(get("/api/sale/store/{storeId}", storeId))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(mockTodaySalesList)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(storeSalesService, times(1)).calculateTodaySalesByPaymentMethod(mockTerminalLists);
    }
}