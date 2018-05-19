package com.example.demo.account.command;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class CloseAccountCommand {
    @TargetAggregateIdentifier
    String id;
}
