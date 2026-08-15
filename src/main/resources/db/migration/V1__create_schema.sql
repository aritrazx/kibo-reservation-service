CREATE TABLE drops (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    total_units INT NOT NULL,
    available_units INT NOT NULL,
    start_time TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_drop_units CHECK (total_units >= 0 AND available_units >= 0 AND available_units <= total_units)
);

CREATE TABLE holds (
    id CHAR(36) PRIMARY KEY,
    drop_id BIGINT NOT NULL,
    customer_id VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_hold_drop FOREIGN KEY (drop_id) REFERENCES drops(id),
    CONSTRAINT chk_hold_quantity CHECK (quantity > 0),
    CONSTRAINT chk_hold_status CHECK (status IN ('ACTIVE','CONFIRMED','CANCELLED','EXPIRED')),
    INDEX idx_holds_expiry_status (status, expires_at),
    INDEX idx_holds_drop (drop_id)
);

CREATE TABLE outbox_events (
    id CHAR(36) PRIMARY KEY,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(100) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP NULL,
    INDEX idx_outbox_unpublished (published_at, created_at)
);
