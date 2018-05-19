package com.example.demo.account.event;

import lombok.Value;

@Value
public class AccountCreatedEvent {
    String id;
    String accountCreator;
    double balance;
}
