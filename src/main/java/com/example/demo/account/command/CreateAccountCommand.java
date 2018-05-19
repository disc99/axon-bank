package com.example.demo.account.command;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateIdentifier;

@Value
public class CreateAccountCommand {
    @TargetAggregateIdentifier
    String id;
    String accountCreator;
}
