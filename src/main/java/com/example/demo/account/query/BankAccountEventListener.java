package com.example.demo.account.query;

import com.example.demo.account.event.AccountClosedEvent;
import com.example.demo.account.event.AccountCreatedEvent;
import com.example.demo.account.event.MoneyDepositedEvent;
import com.example.demo.account.event.MoneyWithdrawnEvent;
import lombok.AllArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BankAccountEventListener {

    BankAccountRepository repository;

    @EventHandler
    public void on(AccountCreatedEvent event) {
        BankAccountEntity entity = new BankAccountEntity(
                event.getId(),
                event.getBalance(),
                event.getAccountCreator()
        );
        repository.save(entity);
    }

    @EventHandler
    public void on(MoneyDepositedEvent event) {
        repository.findById(event.getId())
                .ifPresent(entity -> {
                    entity.setBalance(entity.getBalance() + event.getAmount());
                    repository.save(entity);
                });
    }

    @EventHandler
    public void on(MoneyWithdrawnEvent event) {
        repository.findById(event.getId())
                .ifPresent(entity -> {
                    entity.setBalance(entity.getBalance() - event.getAmount());
                    repository.save(entity);
                });
    }

    @EventHandler
    public void on(AccountClosedEvent event) {
        repository.deleteById(event.getId());
    }
}
