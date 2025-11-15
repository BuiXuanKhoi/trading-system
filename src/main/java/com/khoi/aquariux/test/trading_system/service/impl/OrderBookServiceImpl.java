package com.khoi.aquariux.test.trading_system.service.impl;

import com.khoi.aquariux.test.trading_system.dto.request.OrderRequest;
import com.khoi.aquariux.test.trading_system.engine.coordinate.CoordinateMatchingFactory;
import com.khoi.aquariux.test.trading_system.enumeration.CryptoSymbol;
import com.khoi.aquariux.test.trading_system.enumeration.MatchingStrategy;
import com.khoi.aquariux.test.trading_system.enumeration.OrderStatus;
import com.khoi.aquariux.test.trading_system.enumeration.OrderType;
import com.khoi.aquariux.test.trading_system.exception.UserBalanceNotEnoughException;
import com.khoi.aquariux.test.trading_system.infra.pool.CryptoPoolReadOnly;
import com.khoi.aquariux.test.trading_system.infra.repository.OrderBookRepository;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.OrderBook;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.User;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.Wallet;
import com.khoi.aquariux.test.trading_system.service.OrderBookService;
import com.khoi.aquariux.test.trading_system.service.UserService;
import com.khoi.aquariux.test.trading_system.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderBookServiceImpl implements OrderBookService {

    private final OrderBookRepository orderBookRepository;
    private final UserService userService;
    private final WalletService walletService;
    private final CryptoPoolReadOnly cryptoPool;
    private final CoordinateMatchingFactory coordinateMatchingFactory;

    @Override
    @Transactional
    public OrderBook add(OrderRequest request) {
        log.info("handle place order for user uuid {}", request.userUuid());
        User owner = userService.findUserByUuid(request.userUuid());
        OrderBook orderBook = request.isBuy() ? createBuyOrder(request, owner) : createSellOrder(request, owner);
        OrderBook savedOrderBook = saveAndFlush(orderBook);

        coordinateMatchingFactory.getCoordinateMatchingStrategy(MatchingStrategy.FIFO).pushOrder(orderBook);
        return savedOrderBook;
    }

    @Override
    public List<OrderBook> findAllByUser(UUID userUuid) {
        return null;
    }

    private OrderBook saveAndFlush(OrderBook orderBook){
        String action = Objects.nonNull(orderBook.getId()) ? "UPDATED" : "CREATED";
        OrderBook savedOrderBook = orderBookRepository.saveAndFlush(orderBook);
        log.info("finish {} for order id {}", action, orderBook.getId());

        return savedOrderBook;
    }

    private OrderBook createBuyOrder(OrderRequest request, User owner){
        log.info("create buy order for user uuid {}", owner.getUserUuid());
        CryptoSymbol demandSymbol = request.symbol();
        BigDecimal demandSymbolBuyPrice = cryptoPool.getCurrentBuyPriceBySymbol(demandSymbol);
        BigDecimal demandQuantity = request.quantity();
        BigDecimal costQuantity = calculateRequestUsdtFund(demandQuantity, demandSymbolBuyPrice);

        Wallet demandWallet = walletService.findWalletByUserAndSymbol(owner, CryptoSymbol.USDT);

        validateAndLockFund(costQuantity, CryptoSymbol.USDT, demandWallet);
        log.info("lock fund of user uuid {} at wallet {} with balance {}", owner.getUserUuid(), CryptoSymbol.USDT, costQuantity);

        return OrderBook.builder()
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
                .build();
    }

    private OrderBook createSellOrder(OrderRequest request, User owner){
        log.info("create sell order for user uuid {}", owner.getUserUuid());
        CryptoSymbol demandSymbol = request.symbol();
        BigDecimal demandSymbolSellPrice = cryptoPool.getCurrentSellPriceBySymbol(demandSymbol);
        BigDecimal demandQuantity = request.quantity();
        BigDecimal receiveQuantity = calculateRequestUsdtFund(demandQuantity, demandSymbolSellPrice);

        Wallet demandWallet = walletService.findWalletByUserAndSymbol(owner, demandSymbol);

        validateAndLockFund(demandQuantity, demandSymbol, demandWallet);
        log.info("lock fund of user uuid {} at wallet {} with balance {}", owner.getUserUuid(), demandSymbol, demandQuantity);

        return OrderBook.builder()
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
                .build();
    }


    private void validateAndLockFund(BigDecimal requestedQuantity, CryptoSymbol demandSymbol, Wallet wallet){
        if (wallet.getAvailableBalance().compareTo(requestedQuantity) < 0){
            throw new UserBalanceNotEnoughException("user balance for %s is not enough", demandSymbol.name());
        }

        walletService.acquireLockFund(wallet.getId(), demandSymbol, requestedQuantity);
    }

    private BigDecimal calculateRequestUsdtFund(BigDecimal quantity, BigDecimal price){
        return quantity.multiply(price);
    }
}
