CREATE TABLE currencies (
    code VARCHAR(3) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE exchange_rates (
    id BIGSERIAL PRIMARY KEY,
    base_currency_code VARCHAR(3) NOT NULL REFERENCES currencies(code),
    target_currency_code VARCHAR(3) NOT NULL REFERENCES currencies(code),
    rate DECIMAL(19,6) NOT NULL
);

CREATE INDEX idx_exchange_rates_base ON exchange_rates(base_currency_code);
CREATE INDEX idx_exchange_rates_target ON exchange_rates(target_currency_code);
