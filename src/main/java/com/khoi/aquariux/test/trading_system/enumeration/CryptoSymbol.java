package com.khoi.aquariux.test.trading_system.enumeration;

public enum CryptoSymbol {
    BTC("LTCBTC"),
    ETH("ETHBTC"),
    USDT("USDT");

    final String value;

    CryptoSymbol(final String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
