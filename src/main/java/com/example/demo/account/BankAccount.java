package com.example.demo.account;

import com.example.demo.account.command.CloseAccountCommand;
import com.example.demo.account.command.CreateAccountCommand;
import com.example.demo.account.command.DepositMoneyCommand;
import com.example.demo.account.command.WithdrawMoneyCommand;
import com.example.demo.account.event.AccountClosedEvent;
import com.example.demo.account.event.AccountCreatedEvent;
import com.example.demo.account.event.MoneyDepositedEvent;
import com.example.demo.account.event.MoneyWithdrawnEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.Assert;
import sun.plugin.dom.exception.InvalidStateException;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor // constructor needed for reconstruction
public class BankAccount {

    private static final long serialVersionUID = 1L;

    @AggregateIdentifier
    private String id;

    private double balance;

    private String owner;

    @CommandHandler
    public BankAccount(CreateAccountCommand command) {
        String id = command.getId();
        String name = command.getAccountCreator();

        Assert.hasLength(id, "Missin id");
        Assert.hasLength(name, "Missig account creator");

        apply(new AccountCreatedEvent(id, name, 0));
    }


    @EventSourcingHandler
    protected void on(AccountCreatedEvent event) {
        this.id = event.getId();
        this.owner = event.getAccountCreator();
        this.balance = event.getBalance();
    }

    @CommandHandler
    protected void on(CloseAccountCommand command) {
        apply(new AccountClosedEvent(id));
    }

    @EventSourcingHandler
    protected void on(AccountClosedEvent event) {
        AggregateLifecycle.markDeleted();
    }

    @CommandHandler
    protected void on(DepositMoneyCommand command) {
        double amount = command.getAmount();

        Assert.isTrue(amount > 0.0, "Deposit must be a positiv number.");

        apply(new MoneyDepositedEvent(id, amount));
    }



    @EventSourcingHandler
    protected void on(MoneyDepositedEvent event) {
        this.balance += event.getAmount();
    }

    @CommandHandler
    protected void on(WithdrawMoneyCommand command) {
        double amount = command.getAmount();

        Assert.isTrue(amount > 0.0, "Withdraw must be a positiv number.");

        if(balance - amount < 0) {
            throw new InvalidStateException("Insufficient balance. Trying to withdraw: " + amount + ", but current balance is: " + balance);
        }

        apply(new MoneyWithdrawnEvent(id, amount));
    }


    @EventSourcingHandler
    protected void on(MoneyWithdrawnEvent event) {
        this.balance -= event.getAmount();
    }

}
