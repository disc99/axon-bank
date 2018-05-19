package com.example.demo.account.query;

import lombok.AllArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BankAccountQueryService {

    BankAccountRepository bankAccountRepository;

    @QueryHandler
    public BankAccountDto findById(BankAccountQuery query) {
        return bankAccountRepository.findById(query.getId())
                .map(BankAccountDto::new).orElseThrow(RuntimeException::new);
    }
}
