package com.khoi.aquariux.test.trading_system.engine.matching.strategy;

import com.khoi.aquariux.test.trading_system.engine.TransactionalEngine;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.enumeration.OrderStatus;
import com.khoi.aquariux.test.trading_system.enumeration.OrderType;
import com.khoi.aquariux.test.trading_system.enumeration.TransactionStatus;
import com.khoi.aquariux.test.trading_system.exception.MarketCapacityNotEnoughException;
import com.khoi.aquariux.test.trading_system.exception.ResourceNotFoundException;
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
import org.aspectj.weaver.ast.Or;
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
    private final TransactionalEngine transactionalEngine;

    @Override
    public OrderType getType() {
        return OrderType.MARKET;
    }

    @Override
    public void match(Order order) throws MarketCapacityNotEnoughException{
        Order checkedOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new ResourceNotFoundException("order uuid {} not found", order.getOrderUuid()));
        // Re-fetch order from DB to prevent execution quantity of order not update when push back to queue
        try {
            if (checkedOrder.isBuy())
                matchBuy(checkedOrder);
            else
                matchSell(checkedOrder);

            checkedOrder.setExecutionQuantity(checkedOrder.getRequestQuantity());
            checkedOrder.setStatus(OrderStatus.FILLED);
            orderRepository.saveAndFlush(checkedOrder);
            log.info("finish matching for order uuid {}", checkedOrder.getOrderUuid());
        } catch (UserBalanceNotEnoughException exception){
            log.error("user balance is not enough for exchange order uuid {}", checkedOrder.getOrderUuid());
            checkedOrder.setStatus(OrderStatus.CANCELLED);
            Order savedOrder = orderRepository.saveAndFlush(checkedOrder);
            throw exception;
        } catch (MarketCapacityNotEnoughException exception){
            log.warn("market capacity is not enough for exchange order uuid {}", checkedOrder.getOrderUuid());
            throw exception;
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
            transactionalEngine.execute(() -> handlePartialFilled(order, marketQuantity, marketQuantity.multiply(marketPrice), usdtWallet, targetWallet));
            throw new MarketCapacityNotEnoughException("market capacity not enough");
        }

        transactionalEngine.execute(() -> postProcessFilledOrder(
                marketPrice,
                demandQuantity.multiply(marketPrice),
                CryptoSymbol.USDT,
                marketQuantity,
                usdtWallet,
                targetWallet,
                order
        ));
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
            throw new UserBalanceNotEnoughException("order uuid {} not enough balance to sell {} {}", order.getOrderUuid(), usedQuantity, usedSymbol);
        }

        if (marketSellQuantity.compareTo(usedQuantity) < 0){ // Liquid is exhausted and cannot provide for the order
            transactionalEngine.execute(() -> handlePartialFilled(order, marketSellQuantity.multiply(marketSellPrice), marketSellQuantity, usedWallet, targetWallet));
            throw new MarketCapacityNotEnoughException("market capacity not enough");
        }

        transactionalEngine.execute(() -> postProcessFilledOrder(
                marketSellPrice,
                usedQuantity,
                usedSymbol,
                marketSellQuantity,
                usedWallet,
                targetWallet,
                order
        ));

        BigDecimal receiveQuantity = marketSellPrice.multiply(usedQuantity);

        cryptoPoolWriteOnly.updateSymbolSell(usedSymbol, marketSellQuantity.subtract(usedQuantity));
        walletService.deduct(usedWallet, usedQuantity);
        walletService.depositTo(targetWallet, receiveQuantity);

        transactionService.addNewByOrder(order, TransactionStatus.SUCCESS, receiveQuantity, usedQuantity);
    }

    public void postProcessFilledOrder(
            BigDecimal marketSellPrice,
            BigDecimal usedQuantity,
            CryptoSymbol usedSymbol,
            BigDecimal marketSellQuantity,
            Wallet usedWallet,
            Wallet targetWallet,
            Order order
    ){
        BigDecimal receiveQuantity = marketSellPrice.multiply(usedQuantity);

        cryptoPoolWriteOnly.updateSymbolSell(usedSymbol, marketSellQuantity.subtract(usedQuantity));
        walletService.deduct(usedWallet, usedQuantity);
        walletService.depositTo(targetWallet, receiveQuantity);

        transactionService.addNewByOrder(order, TransactionStatus.SUCCESS, receiveQuantity, usedQuantity);

    }

    public void handlePartialFilled(Order order,
                                     BigDecimal actualExecuteQuantity,
                                     BigDecimal costQuantity,
                                     Wallet sourceWallet,
                                     Wallet targetWallet

    )
    {
        order.setExecutionQuantity(order.getExecutionQuantity().add(actualExecuteQuantity));
        order.setStatus(OrderStatus.PARTIAL_FILLED);
        orderRepository.saveAndFlush(order);

        cryptoPoolWriteOnly.updateSymbolBuy(order.getRequestSymbol(), BigDecimal.ZERO);
        walletService.deduct(sourceWallet, costQuantity);
        walletService.depositTo(targetWallet, actualExecuteQuantity);

        transactionService.addNewByOrder(order, TransactionStatus.SUCCESS, actualExecuteQuantity, costQuantity);
    }
}
