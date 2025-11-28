package com.khoi.aquariux.test.trading_system.service.impl;

import com.khoi.aquariux.test.trading_system.dto.request.OrderRequest;
import com.khoi.aquariux.test.trading_system.dto.response.OrderDetailResponse;
import com.khoi.aquariux.test.trading_system.dto.response.TransactionResponse;
import com.khoi.aquariux.test.trading_system.engine.orderbook.OrderBookFactory;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.enumeration.OrderBookType;
import com.khoi.aquariux.test.trading_system.enumeration.OrderStatus;
import com.khoi.aquariux.test.trading_system.enumeration.OrderType;
import com.khoi.aquariux.test.trading_system.exception.UserBalanceNotEnoughException;
import com.khoi.aquariux.test.trading_system.infra.pool.CryptoPoolReadOnly;
import com.khoi.aquariux.test.trading_system.infra.repository.OrderRepository;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Order;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.User;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Wallet;
import com.khoi.aquariux.test.trading_system.service.OrderService;
import com.khoi.aquariux.test.trading_system.service.UserService;
import com.khoi.aquariux.test.trading_system.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final WalletService walletService;
    private final CryptoPoolReadOnly cryptoPool;
    private final OrderBookFactory orderBookFactory;

    private final static String SELL = "sell";
    private final static String BUY = "buy";


    @Override
    public Order placeMarketOrder(OrderRequest request) {
        User owner = userService.findOrDefault(request.username());

        Order order = request.isBuy() ? createMarketBuyOrder(request, owner) : createMarketSellOrder(request, owner);
        Order savedOrder = orderRepository.saveAndFlush(order);
        orderBookFactory.getOrderBook(OrderBookType.FIFO).pushOrder(savedOrder);
        return savedOrder;
    }

    @Override
    public List<Order> findAllByUser(UUID userUuid) {
        return null;
    }

    @Override
    public void updateStatus(Order order, OrderStatus newStatus) {
        log.info("switch status for order uuid {} from {} to {}", order.getOrderUuid(), order.getStatus(), newStatus);
        order.setStatus(newStatus);
        orderRepository.saveAndFlush(order);
    }

    @Override
    public List<OrderDetailResponse> findAllByUserUuid(UUID userUuid) {
        User user = userService.findUserByUuid(userUuid);

        return orderRepository.getAllByUserId(user.getId())
                .stream()
                .map(this::buildOrderDetailResponse)
                .collect(Collectors.toList());
    }


    private OrderDetailResponse buildOrderDetailResponse(Order order){
        return OrderDetailResponse.builder()
                .orderUuid(order.getOrderUuid())
                .orderStatus(order.getStatus())
                .transactions(
                        order.getTransactions()
                                .stream()
                                .map(TransactionResponse::fromTransaction)
                                .collect(Collectors.toList())
                ).build();
    }

    private Order createMarketBuyOrder(OrderRequest request, User owner){
        log.info("start create buy order for user uuid {}", owner.getUserUuid());
        CryptoSymbol demandSymbol = request.symbol();
        BigDecimal demandSymbolBuyPrice = cryptoPool.getCurrentBuyPriceBySymbol(demandSymbol);
        BigDecimal demandQuantity = request.quantity();
        BigDecimal costQuantity = calculateRequestUsdtFund(demandQuantity, demandSymbolBuyPrice);

        Wallet demandWallet = walletService.findWalletByUserAndSymbol(owner, CryptoSymbol.USDT);
        validate(costQuantity, CryptoSymbol.USDT, demandWallet);

        String operation = request.isBuy() ? BUY : SELL;
        log.info("init {} order for user uuid {} with amount {} {}", operation, owner.getUserUuid(), costQuantity, CryptoSymbol.USDT.name());

        return Order.builder()
                .user(owner)
                .isBuy(true)
                .orderUuid(UUID.randomUUID())
                .orderType(OrderType.MARKET)
                .status(OrderStatus.OPEN)
                .requestSymbol(demandSymbol)
                .requestQuantity(demandQuantity)
                .usedSymbol(CryptoSymbol.USDT)
                .usedQuantity(costQuantity)
                .limitPrice(demandSymbolBuyPrice)
                .executionQuantity(BigDecimal.ZERO)
                .build();
    }

    private Order createMarketSellOrder(OrderRequest request, User owner){
        log.info("create sell order for user uuid {}", owner.getUserUuid());
        CryptoSymbol demandSymbol = request.symbol();
        BigDecimal demandSymbolSellPrice = cryptoPool.getCurrentSellPriceBySymbol(demandSymbol);
        BigDecimal demandQuantity = request.quantity();
        BigDecimal receiveQuantity = calculateRequestUsdtFund(demandQuantity, demandSymbolSellPrice);

        Wallet demandWallet = walletService.findWalletByUserAndSymbol(owner, demandSymbol);
        validate(demandQuantity, demandSymbol, demandWallet);

        String operation = request.isBuy() ? BUY : SELL;
        log.info("create {} order for user uuid {} with amount {} {}", operation, owner.getUserUuid(), demandQuantity, demandSymbol);

        return Order.builder()
                .user(owner)
                .isBuy(false)
                .orderUuid(UUID.randomUUID())
                .orderType(OrderType.MARKET)
                .status(OrderStatus.OPEN)
                .requestSymbol(CryptoSymbol.USDT)
                .requestQuantity(receiveQuantity)
                .usedSymbol(demandSymbol)
                .usedQuantity(demandQuantity)
                .limitPrice(demandSymbolSellPrice)
                .executionQuantity(BigDecimal.ZERO)
                .build();
    }

    private void validate(BigDecimal requestedQuantity, CryptoSymbol demandSymbol, Wallet wallet){
        if (wallet.getAvailableBalance().compareTo(requestedQuantity) < 0){
            throw new UserBalanceNotEnoughException("user balance for %s is not enough", demandSymbol.name());
        }
    }

    private BigDecimal calculateRequestUsdtFund(BigDecimal quantity, BigDecimal price){
        return quantity.multiply(price);
    }
}
