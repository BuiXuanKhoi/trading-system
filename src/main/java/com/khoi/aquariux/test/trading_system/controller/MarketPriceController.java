package com.khoi.aquariux.test.trading_system.controller;

import com.khoi.aquariux.test.trading_system.dto.response.BestMarketPriceResponse;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.service.MarketPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/market-price")
@RequiredArgsConstructor
@Validated
public class MarketPriceController {


    private final MarketPriceService marketPriceService;

    @GetMapping("/latest")
    public Map<CryptoSymbol, BestMarketPriceResponse> getBestMarketPrice(){
        return this.marketPriceService.getLatestBestMarketPrice();
    }
}
