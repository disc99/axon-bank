package com.example.demo.account.event;

import lombok.Value;

@Value
public class MoneyDepositedEvent {
    String id;
    double amount;
}
