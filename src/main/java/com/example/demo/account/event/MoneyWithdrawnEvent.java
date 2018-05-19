package com.example.demo.account.event;

import lombok.Value;

@Value
public class MoneyWithdrawnEvent {
    String id;
    double amount;
}
