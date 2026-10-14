package com.aksana.personalfinance.service;

import com.aksana.personalfinance.entity.Account;
import com.aksana.personalfinance.entity.User;
import com.aksana.personalfinance.repository.AccountRepository;
import com.aksana.personalfinance.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(
            AccountRepository accountRepository,
            UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsForUser(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    @Transactional
    public Account createAccount(Long userId, Account account) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        account.setUser(user);

        return accountRepository.save(account);
    }
}
