package com.khoi.aquariux.test.trading_system.controller;


import com.khoi.aquariux.test.trading_system.dto.response.WalletBalanceResponse;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Wallet;
import com.khoi.aquariux.test.trading_system.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@Validated
public class WalletController {


    private final WalletService walletService;

    @GetMapping("/{userUuid}")
    public Map<CryptoSymbol, WalletBalanceResponse> getUserWalletBalance(
            @PathVariable("userUuid")UUID userUuid
            ){
        return this.walletService.getWalletsByUserUuid(userUuid);
    }
}
