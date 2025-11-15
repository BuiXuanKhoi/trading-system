package com.khoi.aquariux.test.trading_system.service;

import com.khoi.aquariux.test.trading_system.dto.request.OrderRequest;
import com.khoi.aquariux.test.trading_system.engine.coordinate.CoordinateFIFOMatchingStrategy;
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
import com.khoi.aquariux.test.trading_system.service.impl.OrderBookServiceImpl;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderBookServiceTest {


    @Mock
    private UserService userService;
    @Mock
    private WalletService walletService;
    @Mock
    private OrderBookRepository orderBookRepository;

    @Mock
    private CryptoPoolReadOnly cryptoPoolReadOnly;

    @InjectMocks
    private OrderBookServiceImpl orderBookService;

    @Mock
    private CoordinateMatchingFactory coordinateMatchingFactory;

    @Mock
    private CoordinateFIFOMatchingStrategy coordinateFIFOMatchingStrategy;

    @Test
    void testAddBuyOrder_successfully(){
        UUID userUuId = UUID.randomUUID();

        OrderRequest request = new OrderRequest(
                userUuId,
                true,
                OrderType.MARKET,
                CryptoSymbol.BTC,
                BigDecimal.ONE
        );

        BigDecimal ratePrice = BigDecimal.ONE;

        User user = User.builder()
                .userUuid(userUuId).username("khoi").build();
        Wallet wallet = Wallet.builder()
                .user(user).availableBalance(BigDecimal.TEN).lockedBalance(BigDecimal.ZERO).symbol(CryptoSymbol.USDT).build();

        OrderBook orderBook = OrderBook.builder()
                .user(user)
                .isBuy(true)
                .orderUuid(UUID.randomUUID())
                .orderType(OrderType.MARKET)
                .status(OrderStatus.OPEN)
                .requestSymbol(request.symbol())
                .requestQuantity(request.quantity())
                .usedSymbol(CryptoSymbol.USDT)
                .usedQuantity(request.quantity().multiply(ratePrice))
                .limitPrice(request.quantity().multiply(ratePrice))
                .build();

        when(userService.findUserByUuid(userUuId)).thenReturn(user);
        when(walletService.findWalletByUserAndSymbol(any(), any())).thenReturn(wallet);
        when(cryptoPoolReadOnly.getCurrentBuyPriceBySymbol(CryptoSymbol.BTC)).thenReturn(ratePrice);
        when(coordinateMatchingFactory.getCoordinateMatchingStrategy(MatchingStrategy.FIFO)).thenReturn(coordinateFIFOMatchingStrategy);
        when(orderBookRepository.saveAndFlush(any())).thenReturn(orderBook);

        OrderBook actualResult = orderBookService.add(request);

        assertEquals(orderBook.getOrderType(), actualResult.getOrderType());
        assertEquals(user.getId(), actualResult.getUser().getId());
        assertEquals(request.symbol(), actualResult.getRequestSymbol());
        assertEquals(request.quantity(), actualResult.getRequestQuantity());
        assertEquals(CryptoSymbol.USDT, actualResult.getUsedSymbol());
        assertEquals(request.quantity().multiply(ratePrice), actualResult.getUsedQuantity());
    }

    @Test
    void testAddBuyOrder_shouldThrowUserBalanceNotEnoughException_ifAvailableBalanceLessThanRequestQuantity(){
        UUID userUuId = UUID.randomUUID();

        OrderRequest request = new OrderRequest(
                userUuId,
                true,
                OrderType.MARKET,
                CryptoSymbol.BTC,
                BigDecimal.TEN
        );

        User user = User.builder()
                        .userUuid(userUuId).username("khoi").build();
        Wallet wallet = Wallet.builder()
                        .user(user).availableBalance(BigDecimal.ONE).lockedBalance(BigDecimal.ZERO).symbol(CryptoSymbol.USDT).build();

        when(userService.findUserByUuid(userUuId)).thenReturn(user);
        when(walletService.findWalletByUserAndSymbol(any(), any())).thenReturn(wallet);
        when(cryptoPoolReadOnly.getCurrentBuyPriceBySymbol(CryptoSymbol.BTC)).thenReturn(BigDecimal.TEN);

        assertThrows(UserBalanceNotEnoughException.class, () -> orderBookService.add(request));
    }
}
