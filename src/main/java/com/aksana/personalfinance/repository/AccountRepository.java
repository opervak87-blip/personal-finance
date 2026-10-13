package com.aksana.personalfinance.repository;

import com.aksana.personalfinance.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
