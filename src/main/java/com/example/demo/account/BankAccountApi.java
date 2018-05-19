package com.example.demo.account;

import com.example.demo.account.command.CloseAccountCommand;
import com.example.demo.account.command.CreateAccountCommand;
import com.example.demo.account.command.DepositMoneyCommand;
import com.example.demo.account.command.WithdrawMoneyCommand;
import com.example.demo.account.query.BankAccountDto;
import com.example.demo.account.query.BankAccountQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.Message;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;

@RequestMapping("/accounts")
@RestController
@AllArgsConstructor
class BankAccountApi {

    EventStore eventStore;
    CommandGateway commandGateway;
    QueryGateway queryGateway;

    @GetMapping("{id}/events")
    List<Object> getEvents(@PathVariable String id) {
        return eventStore.readEvents(id).asStream()
                .map(Message::getPayload)
                .collect(toList());
    }

    @PostMapping
    CompletableFuture<String> createAccount(@RequestBody AccountOwner user) {
        String id = UUID.randomUUID().toString();
        return commandGateway.send(new CreateAccountCommand(id, user.getName()));
    }

    @PutMapping(path = "{accountId}/balance")
    CompletableFuture<String> deposit(@RequestBody Balance balance, @PathVariable String accountId) {
        if (balance.getAmount() > 0) {
            return commandGateway.send(new DepositMoneyCommand(accountId, balance.getAmount()));
        } else {
            return commandGateway.send(new WithdrawMoneyCommand(accountId, - balance.getAmount()));
        }
    }

    @DeleteMapping("{id}")
    CompletableFuture<String> delete(@PathVariable String id) {
        return commandGateway.send(new CloseAccountCommand(id));
    }

    @GetMapping("{id}")
    CompletableFuture<BankAccountDto> findById(@PathVariable String id) {
        BankAccountQuery query = new BankAccountQuery(id);
        return queryGateway.query(query, BankAccountDto.class);
    }

    @Data
    static class Balance {
        double amount;
    }

    @Data
    static class AccountOwner {
        String name;
    }
}
