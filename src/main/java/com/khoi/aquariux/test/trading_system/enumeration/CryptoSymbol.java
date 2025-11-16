package com.khoi.aquariux.test.trading_system.enumeration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum CryptoSymbol {

    BTCUSDT,
    ETHUSDT,
    USDT;

    public static Optional<CryptoSymbol> getSymbolByName(String name){
        return Arrays.stream(values()).filter(symbol -> symbol.name().equalsIgnoreCase(name))
                .findFirst();
    }

    public static List<CryptoSymbol> getUpdateAbleCryptoSymbol(){
        return List.of(BTCUSDT, ETHUSDT);
    }
}
