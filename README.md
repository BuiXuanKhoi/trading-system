

# Trading System

### 1. The requirements

#### 1.1 Description

Develop a crypto trading system with SpringBoot framework and in memory H2


#### 1.2. Functional scope

   1. User able to buy/sell the supported crypto trading pairs
   2. User able to see the list of trading transactions
   3. User able to see the crypto currencies wallet balance


#### 1.3. Assumption

- User has already authenticated and authorised to access the APIs
- User's initial wallet balance 50,000 USDT in DB record.
- Only support Ethereum - ETHUSDT and Bitcoin - BTCUSDT pairs of crypto trading.
- User will trade based on the best aggregated price provided by the market feeding.
    -> We would compare bid/ask price between Huobi and Binance platform to get best aggregated price

### 2. System design

#### 2.1 High level design

- The order must follow FIFO rule - which mean the first order will be executed first
- When the order come and the order queue is empty, the order must be executed immediately
- When user place an order but the quantity is not enough, it will be reversed

##### 2.1.1 Core logic

- The market order is type of order that will be executed with the best market price at the time it executed.
- All market order that cannot full filled at once time will be marked at PARTIAL_FILLED and can be handle later. But the order still prior time
- In the time market order being executed, if the balance is not enough to execute the order, the order will be marked as CANCELLED
##### 2.1.1 Order Book

- At here, I use the FIFO type of order book, which mean it will prior the time an order pushed to queue instead
- The order book used for user placed an market order. The order will be executed based on the time it inputted.

##### 2.1.2 Matching Engine

- The matching engine match market order based on the current market price and quantity.
- If market quantity is not enough to filled the order. The order could be change to PARTIAL_FILLED and the remaining order could be filled in the next time

![img_1.png](img_1.png)

#### 2.2 Database design

![img_4.png](img_4.png)

2.3 Low level design