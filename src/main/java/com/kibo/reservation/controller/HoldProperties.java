package com.kibo.reservation.controller;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "reservation")
public class HoldProperties {
    private long holdDurationSeconds = 300;
    public long durationSeconds() { return holdDurationSeconds; }
    public void setHoldDurationSeconds(long value) { this.holdDurationSeconds = value; }
}
