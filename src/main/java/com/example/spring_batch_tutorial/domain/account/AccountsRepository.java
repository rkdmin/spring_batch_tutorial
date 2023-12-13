package com.example.spring_batch_tutorial.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsRepository extends JpaRepository<Accounts, Integer> {
}
