package com.khoi.aquariux.test.trading_system.engine.matching.strategy;

import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.enumeration.OrderStatus;
import com.khoi.aquariux.test.trading_system.enumeration.OrderType;
import com.khoi.aquariux.test.trading_system.exception.MarketCapacityNotEnoughException;
import com.khoi.aquariux.test.trading_system.exception.UserBalanceNotEnoughException;
import com.khoi.aquariux.test.trading_system.infra.pool.connector.dto.CryptoPriceResponse;
import com.khoi.aquariux.test.trading_system.infra.pool.CryptoPoolWriteOnly;
import com.khoi.aquariux.test.trading_system.infra.repository.OrderRepository;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Wallet;
import com.khoi.aquariux.test.trading_system.service.TransactionService;
import com.khoi.aquariux.test.trading_system.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
@Log4j2
public class MarketMatchingStrategy implements MatchingStrategy{

    private final CryptoPoolWriteOnly cryptoPoolWriteOnly;
    private final TransactionService transactionService;
    private final WalletService walletService;
    private final OrderRepository orderRepository;

    @Override
    public OrderType getType() {
        return OrderType.MARKET;
    }

    @Override
    @Async
    @Transactional
    public void match(Order order) {
        OrderStatus newOrderStatus = OrderStatus.FILLED;
        try {
            if (order.isBuy())
                matchBuy(order);
            else
                matchSell(order);
            log.info("finish matching for order with uuid {}", order.getOrderUuid());
        } catch (UserBalanceNotEnoughException exception){
            log.error("user balance is not enough for exchange order id {}", order.getOrderUuid());
            newOrderStatus = OrderStatus.CANCELLED;
        } catch (MarketCapacityNotEnoughException exception){
            log.warn("market capacity is not enough for exchange order uuid {}", order.getOrderUuid());
            newOrderStatus = OrderStatus.PARTIAL_FILLED;
            throw exception;
        } finally {
            order.setStatus(newOrderStatus);
            orderRepository.saveAndFlush(order);
        }
    }

    private void matchBuy(Order order){
        log.info("start matching for buy order with uuid {}", order.getOrderUuid());

        BigDecimal demandQuantity = order.getRequestQuantity().subtract(order.getExecutionQuantity());
        CryptoSymbol demandSymbol = order.getRequestSymbol();
        Wallet usdtWallet = walletService.findWalletByUserAndSymbol(order.getUser(), CryptoSymbol.USDT);
        Wallet targetWallet = walletService.findWalletByUserAndSymbol(order.getUser(), demandSymbol);

        CryptoPriceResponse.SymbolInfo marketSymbol = cryptoPoolWriteOnly.getSymbolInfoBySymbol(demandSymbol);
        BigDecimal marketQuantity = marketSymbol.getAskQuantity();
        BigDecimal marketPrice = marketSymbol.getAskPrice();

        BigDecimal maxBuyQuantity = usdtWallet.getAvailableBalance().divide(marketPrice, RoundingMode.HALF_UP);

        if (maxBuyQuantity.compareTo(demandQuantity) < 0){
            throw new UserBalanceNotEnoughException("user uuid {} not enough balance to buy {} {}", order.getUser().getUserUuid(), demandQuantity, demandSymbol);
        }

        if(marketQuantity.compareTo(demandQuantity) < 0 ){
            split(order);
            throw new MarketCapacityNotEnoughException("market capacity not enough");
        }

        BigDecimal costQuantity = demandQuantity.multiply(marketPrice);

        cryptoPoolWriteOnly.updateSymbolBuy(demandSymbol, marketQuantity.subtract(demandQuantity));
        walletService.deduct(usdtWallet, costQuantity);
        walletService.depositTo(targetWallet, demandQuantity);

        transactionService.addNewByOrder(order, demandQuantity, costQuantity);
    }

    private void matchSell(Order order){
        log.info("start matching for sell order with uuid {}", order.getOrderUuid());

        BigDecimal usedQuantity = order.getUsedQuantity().subtract(order.getExecutionQuantity());
        CryptoSymbol usedSymbol = order.getUsedSymbol();
        Wallet usedWallet = walletService.findWalletByUserAndSymbol(order.getUser(), usedSymbol);
        Wallet targetWallet = walletService.findWalletByUserAndSymbol(order.getUser(), CryptoSymbol.USDT);

        CryptoPriceResponse.SymbolInfo marketSymbol = cryptoPoolWriteOnly.getSymbolInfoBySymbol(usedSymbol);
        BigDecimal marketSellQuantity = marketSymbol.getBidQuantity();
        BigDecimal marketSellPrice = marketSymbol.getBidPrice();

        if (usedWallet.getAvailableBalance().compareTo(usedQuantity) < 0){
            throw new UserBalanceNotEnoughException("user uuid {} not enough balance to sell {} {}", order.getUser().getUserUuid(), usedQuantity, usedSymbol);
        }

        if (marketSellQuantity.compareTo(usedQuantity) < 0){ // Liquid is exhausted and cannot provide for the order
            split(order);
            throw new MarketCapacityNotEnoughException("market capacity not enough");
        }

        BigDecimal receiveQuantity = marketSellPrice.multiply(usedQuantity);

        cryptoPoolWriteOnly.updateSymbolSell(usedSymbol, marketSellQuantity.subtract(usedQuantity));
        walletService.deduct(usedWallet, usedQuantity);
        walletService.depositTo(targetWallet, receiveQuantity);

        transactionService.addNewByOrder(order, receiveQuantity, usedQuantity);
    }

    private void split(Order order){

    }
}
