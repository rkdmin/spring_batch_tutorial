package com.example.spring_batch_tutorial.domain.orders;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {

}
