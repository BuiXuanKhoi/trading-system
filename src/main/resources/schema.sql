SET MODE MYSQL;
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255),
    user_uuid VARCHAR(45),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    available_balance DECIMAL(20, 8) NOT NULL,
    locked_balance DECIMAL(20, 8) NOT NULL,
    user_id BIGINT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uni_wallet_user_symbol UNIQUE (user_id, symbol)
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    used_quantity DECIMAL(20, 8),
    used_symbol VARCHAR(20),
    request_symbol VARCHAR(20),
    request_quantity DECIMAL(20, 8) NOT NULL,
    status VARCHAR(15) NOT NULL,
    is_buy BOOLEAN NOT NULL,
    order_uuid VARCHAR(45) NOT NULL,
    order_type VARCHAR(20) NOT NULL,
    limit_price DECIMAL(20, 8) NOT NULL,
    execution_quantity DECIMAL(20, 8) DEFAULT 0,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_book_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_uuid VARCHAR(45),
    is_buy BOOLEAN NOT NULL,
    used_symbol VARCHAR(20) NOT NULL,
    used_quantity DECIMAL(20, 8) NOT NULL,
    request_symbol VARCHAR(20) NOT NULL,
    request_quantity DECIMAL(20, 8) NOT NULL,
    status VARCHAR(10) NOT NULL,
    order_id BIGINT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_order FOREIGN KEY (order_id) REFERENCES orders (id)
);

CREATE TABLE IF NOT EXISTS market_price (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(20),
    bid_price DECIMAL(20, 8) NOT NULL,
    bid_quantity DECIMAL(20, 8) NOT NULL,
    bid_source VARCHAR(10) NOT NULL,
    ask_price DECIMAL(20, 8) NOT NULL,
    ask_quantity DECIMAL(20, 8) NOT NULL,
    ask_source VARCHAR(10) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
